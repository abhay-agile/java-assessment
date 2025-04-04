package com.abhay.assessment.java_assessment.student.service;

import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import com.abhay.assessment.java_assessment.common.util.CommonUtil;
import com.abhay.assessment.java_assessment.student.dto.SemesterWiseGradePointDto;
import com.abhay.assessment.java_assessment.student.dto.StudentGradePointResponseDto;
import com.abhay.assessment.java_assessment.student.dto.StudentMarksResponseDto;
import com.abhay.assessment.java_assessment.student.dto.SubmitMarksDto;
import com.abhay.assessment.java_assessment.student.model.Marks;
import com.abhay.assessment.java_assessment.student.model.ResultStatus;
import com.abhay.assessment.java_assessment.student.model.Subject;
import com.abhay.assessment.java_assessment.student.repository.GradePointRepository;
import com.abhay.assessment.java_assessment.student.repository.MarksRepository;
import com.abhay.assessment.java_assessment.student.repository.SubjectRepository;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class MarksService {

    private final MarksRepository marksRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final MongoTemplate mongoTemplate;

    public MarksService(
            MarksRepository marksRepository,
            UserRepository userRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            MongoTemplate mongoTemplate
    ) {
        this.marksRepository = marksRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private List<AggregationOperation> commonMarksAggregationStage() {
        return List.of(
                Aggregation.lookup("subject", "subjectId", "_id", "subject"),
                Aggregation.unwind("subject", true),
                Aggregation.lookup("users", "studentId", "_id", "student"),
                Aggregation.unwind("student", false),
                Aggregation.project(
                                "id",
                                "semester",
                                "studentId",
                                "subjectGradePoint",
                                "subjectStatus"
                        )
                        .and("subject.name").as("subjectName")
                        .and("subject.creditPoint").as("subjectCredit")
                        .and("student.enrollmentNumber").as("studentEnrollmentNumber")
                        .and("student.name").as("studentName")
                        .and("student.email").as("studentEmail")
                        .and(
                                ArithmeticOperators.Multiply
                                        .valueOf("subject.creditPoint")
                                        .multiplyBy("subjectGradePoint")
                        ).as("overAllGradePoint"),
                Aggregation.group("semester")
                        .first("semester").as("semester")
                        .first("studentId").as("studentId")
                        .first("studentName").as("studentName")
                        .first("studentEmail").as("studentEmail")
                        .first("studentEnrollmentNumber").as("studentEnrollmentNumber")
                        .push(
                                new BasicDBObject("subjectName", "$subjectName")
                                        .append("subjectCredit", "$subjectCredit")
                                        .append("subjectGradePoint", "$subjectGradePoint")
                                        .append("subjectStatus", "$subjectStatus")
                                        .append("overAllGradePoint", "$overAllGradePoint")
                        ).as("marks"),
                Aggregation.sort(Sort.Direction.ASC, "semester"),
                Aggregation.group("studentId")
                        .first("studentName").as("studentName")
                        .first("studentEmail").as("studentEmail")
                        .first("studentEnrollmentNumber").as("studentEnrollmentNumber")
                        .push(
                                new BasicDBObject("semester", "$semester")
                                        .append("marks", "$marks")
                        ).as("result")
        );
    }

    private List<AggregationOperation> commonGradePointAggregationStage() {
        return List.of(
                Aggregation.lookup("subject", "subjectId", "_id", "subject"),
                Aggregation.unwind("subject", true),
                Aggregation.lookup("users", "studentId", "_id", "student"),
                Aggregation.unwind("student", false),
                Aggregation.addFields()
                        .addField("subjectOverallPoint").withValue(
                                ArithmeticOperators.Multiply.valueOf("$subjectGradePoint")
                                        .multiplyBy("$subject.creditPoint")
                        ).build(),
                Aggregation.group("semester")
                        .first("semester").as("semester")
                        .first("studentId").as("studentId")
                        .first("student.name").as("studentName")
                        .first("student.email").as("studentEmail")
                        .first("student.enrollmentNumber").as("studentEnrollmentNumber")
                        .sum("subjectOverallPoint").as("overAllMarks")
                        .sum("subject.creditPoint").as("overAllCredit"),
                Aggregation.project(
                        "semester",
                        "studentId",
                        "studentName",
                        "studentEmail",
                        "studentEnrollmentNumber"
                ).and(
                        ArithmeticOperators.Divide
                                .valueOf("$overAllMarks")
                                .divideBy("$overAllCredit")
                ).as("gpa"),
                Aggregation.sort(Sort.Direction.ASC, "semester"),
                Aggregation.group("studentId")
                        .first("studentName").as("studentName")
                        .first("studentEmail").as("studentEmail")
                        .first("studentEnrollmentNumber").as("studentEnrollmentNumber")
                        .sum("gpa").as("gpaTotal")
                        .count().as("totalSemester")
                        .push(
                                new BasicDBObject("semester", "$semester")
                                        .append("gpa", "$gpa")
                        ).as("result"),
                Aggregation.addFields()
                        .addField("cgpa").withValue(
                                ArithmeticOperators.Divide
                                        .valueOf("$gpaTotal")
                                        .divideBy("$totalSemester")
                        )
                        .build()
        );
    }

    public String getLoggedInUserId() {
        String userEmail = CommonUtil.getLoggedInUserEmail();

        Optional<User> user = userRepository.findByEmail(userEmail);

        return user.map(User::getId).orElse(null);
    }

    public Marks submitMarks(SubmitMarksDto submitMarksDto) {
        Optional<Student> student = studentRepository.findById(submitMarksDto.getStudentId());
        Optional<Subject> subject = subjectRepository.findById(submitMarksDto.getSubjectId());
        List<Marks> marksList = marksRepository.findByStudentIdAndSubjectId(
                new ObjectId(submitMarksDto.getStudentId()),
                new ObjectId(submitMarksDto.getSubjectId())
        );

        if (student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        if (subject.get().getSemester() != submitMarksDto.getSemester()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Semester you entered is not match with subject's semester.");
        }

        if (submitMarksDto.getObtainedMarks() > 100) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Obtained marks should not exceeds to 100.");
        }

        if (!marksList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This subject marks for this user is already submitted.");
        }

        float subjectGradePoint = (float) submitMarksDto.getObtainedMarks() / 10;
        ResultStatus status = subjectGradePoint >= 5.0 ? ResultStatus.PASS : ResultStatus.FAIL;

        Marks newMarks = new Marks();
        newMarks.setStudentId(new ObjectId(submitMarksDto.getStudentId()));
        newMarks.setSubjectId(new ObjectId(submitMarksDto.getSubjectId()));
        newMarks.setSemester(submitMarksDto.getSemester());
        newMarks.setSubjectGradePoint(subjectGradePoint);
        newMarks.setSubjectStatus(status);

        return marksRepository.save(newMarks);
    }

    public List<StudentMarksResponseDto> viewSemesterWiseStudentMarks(String studentId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        Stream.of(
                                Aggregation.match(Criteria.where("studentId").is(new ObjectId(studentId)))
                        ),
                        commonMarksAggregationStage().stream()
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentMarksResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "marks",
                StudentMarksResponseDto.class
        ).getMappedResults();

        return results;
    }

    public List<StudentMarksResponseDto> viewAllStudentMarks() {
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        commonMarksAggregationStage().stream(),
                        Stream.of(
                                Aggregation.sort(Sort.Direction.ASC, "studentEnrollmentNumber")
                        )
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentMarksResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "marks",
                StudentMarksResponseDto.class
        ).getMappedResults();

        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        return results;
    }

    public List<StudentGradePointResponseDto> getStudentGradePoint(String studentId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        Stream.of(
                                Aggregation.match(Criteria.where("studentId").is(new ObjectId(studentId)))
                        ),
                        commonGradePointAggregationStage().stream()
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentGradePointResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "marks",
                StudentGradePointResponseDto.class
        ).getMappedResults();

        return results;
    }

    public List<StudentGradePointResponseDto> getAllStudentGradePoint() {
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        commonGradePointAggregationStage().stream(),
                        Stream.of(
                                Aggregation.sort(Sort.Direction.ASC, "studentEnrollmentNumber")
                        )
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentGradePointResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "marks",
                StudentGradePointResponseDto.class
        ).getMappedResults();

        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        return results;
    }
}
