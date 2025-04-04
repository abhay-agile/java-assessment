package com.abhay.assessment.java_assessment.student.service;

import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import com.abhay.assessment.java_assessment.common.util.CommonUtil;
import com.abhay.assessment.java_assessment.student.dto.StudentAttendanceResponseDto;
import com.abhay.assessment.java_assessment.student.dto.SubmitAttendanceRequestDto;
import com.abhay.assessment.java_assessment.student.model.Attendance;
import com.abhay.assessment.java_assessment.student.model.Subject;
import com.abhay.assessment.java_assessment.student.repository.AttendanceRepository;
import com.abhay.assessment.java_assessment.student.repository.SubjectRepository;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AttendanceService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MongoTemplate mongoTemplate;

    public AttendanceService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            AttendanceRepository attendanceRepository,
            MongoTemplate mongoTemplate
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    private List<AggregationOperation> commonAttendanceAggregationStages() {
        return List.of(
                Aggregation.lookup("users", "studentId", "_id", "student"),
                Aggregation.unwind("student", false),
                Aggregation.lookup("subject", "subjectId", "_id", "subject"),
                Aggregation.unwind("subject", true),
                Aggregation.group(
                                Fields.from(
                                        Fields.field("studentId", "$studentId"),
                                        Fields.field("subjectId", "$subjectId")
                                ))
                        .first("subject.name").as("subjectName")
                        .first("subject.semester").as("semester")
                        .first("studentId").as("studentId")
                        .first("student.name").as("studentName")
                        .first("student.email").as("studentEmail")
                        .first("student.enrollmentNumber").as("studentEnrollmentNumber")
                        .sum(ConditionalOperators.when(Criteria.where("attendLecture").is(true))
                                .then(1)
                                .otherwise(0))
                        .as("attendLecture")
                        .count().as("totalLecture"),
                Aggregation.addFields()
                        .addField("subjectAttendance")
                        .withValue(
                                ArithmeticOperators.Multiply.valueOf(
                                        ArithmeticOperators.Divide.valueOf("$attendLecture").divideBy("$totalLecture")
                                ).multiplyBy(100)
                        )
                        .build(),
                Aggregation.group(
                                Fields.from(
                                        Fields.field("studentId", "$studentId"),
                                        Fields.field("semester", "$semester")
                                ))
                        .first("studentId").as("studentId")
                        .first("studentName").as("studentName")
                        .first("studentEmail").as("studentEmail")
                        .first("studentEnrollmentNumber").as("studentEnrollmentNumber")
                        .first("semester").as("semester")
                        .sum("attendLecture").as("totalAttendLecture")
                        .sum("totalLecture").as("totalLecture")
                        .push(
                                new BasicDBObject("subjectName", "$subjectName")
                                        .append("attendLecture", "$attendLecture")
                                        .append("totalLecture", "$totalLecture")
                                        .append("attendance", "$subjectAttendance")
                        ).as("subject"),
                Aggregation.sort(Sort.Direction.ASC, "semester"),
                Aggregation.addFields()
                        .addField("semesterAttendance")
                        .withValue(
                                ArithmeticOperators.Multiply.valueOf(
                                        ArithmeticOperators.Divide.valueOf("$totalAttendLecture").divideBy("$totalLecture")
                                ).multiplyBy(100)
                        )
                        .build(),
                Aggregation.group("studentId")
                        .first("studentId").as("studentId")
                        .first("studentName").as("studentName")
                        .first("studentEmail").as("studentEmail")
                        .first("studentEnrollmentNumber").as("studentEnrollmentNumber")
                        .push(
                                new BasicDBObject("semester", "$semester")
                                        .append("totalAttendLecture", "$totalAttendLecture")
                                        .append("totalLecture", "$totalLecture")
                                        .append("subject", "$subject")
                                        .append("attendance", "$semesterAttendance")
                        ).as("attendance")
        );
    }

    public String getLoggedInUserId() {
        String userEmail = CommonUtil.getLoggedInUserEmail();

        Optional<User> user = userRepository.findByEmail(userEmail);

        return user.map(User::getId).orElse(null);
    }

    public void submitAttendance(SubmitAttendanceRequestDto requestDto) {
        LocalDate finalDate = requestDto.getLectureDate() == null
                ? LocalDate.now()
                : requestDto.getLectureDate();

        ZoneId zoneUTC = ZoneId.of("UTC");

        Date lectureDate = Date.from(finalDate.atStartOfDay(zoneUTC).toInstant());
        Date startOfDay = Date.from(finalDate.atStartOfDay(zoneUTC).toInstant());
        Date endOfDay = Date.from(finalDate.plusDays(1).atStartOfDay(zoneUTC).toInstant());

        Optional<Attendance> attendanceExist = attendanceRepository.findBySubjectIdAndDateOfLecture(
                requestDto.getSubjectId(),
                startOfDay,
                endOfDay
        );

        Optional<Subject> subjectExist = subjectRepository.findById(requestDto.getSubjectId());

        if (attendanceExist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This subject's attendance is already submitted for today.");
        }

        if (subjectExist.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        if (subjectExist.get().getSemester() != requestDto.getSemester()) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Subject is not from the semester you entered.");
        }

        if (!requestDto.getPresentStudents().isEmpty()) {
            requestDto.getPresentStudents().forEach((String id) -> {
                boolean exist = studentRepository.existsByIdAndCurrentSemester(id, requestDto.getSemester());

                if (!exist) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Some of the student not found."
                    );
                }
            });
        }

        List<Student> semesterWiseStudents = studentRepository.findAllByCurrentSemester(requestDto.getSemester());
        List<Attendance> studentAttendances = new ArrayList<>();

        semesterWiseStudents.forEach((Student student) -> {
            boolean attendLecture = requestDto.getPresentStudents().contains(student.getId());

            studentAttendances.add(
                    new Attendance(
                            new ObjectId(student.getId()),
                            new ObjectId(requestDto.getSubjectId()),
                            attendLecture,
                            lectureDate
                    )
            );
        });

        attendanceRepository.saveAll(studentAttendances);
    }

    public List<StudentAttendanceResponseDto> getStudentAttendance(String studentId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        Stream.of(
                                Aggregation.match(Criteria.where("studentId").is(new ObjectId(studentId)))
                        ),
                        commonAttendanceAggregationStages().stream()
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentAttendanceResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "attendance",
                StudentAttendanceResponseDto.class
        ).getMappedResults();

        return results;
    }

    public List<StudentAttendanceResponseDto> getAllStudentAttendance() {
        Aggregation aggregation = Aggregation.newAggregation(
                Stream.concat(
                        commonAttendanceAggregationStages().stream(),
                        Stream.of(
                                Aggregation.sort(Sort.Direction.ASC, "studentEnrollmentNumber")
                        )
                ).toArray(AggregationOperation[]::new)
        );

        List<StudentAttendanceResponseDto> results = mongoTemplate.aggregate(
                aggregation,
                "attendance",
                StudentAttendanceResponseDto.class
        ).getMappedResults();

        if (results.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        return results;
    }
}
