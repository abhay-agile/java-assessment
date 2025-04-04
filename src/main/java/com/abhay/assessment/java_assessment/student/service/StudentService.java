package com.abhay.assessment.java_assessment.student.service;


import com.abhay.assessment.java_assessment.admin.dto.RequestRegisterDto;
import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import com.abhay.assessment.java_assessment.common.util.CommonUtil;
import com.abhay.assessment.java_assessment.student.dto.StudentResponseDto;
import com.abhay.assessment.java_assessment.student.dto.UpdateUserDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public StudentService(
            StudentRepository studentRepository,
            UserRepository userRepository,
            MongoTemplate mongoTemplate
    ) {
        super();
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public User createStudent(RequestRegisterDto registerDto) {

        String encodedPassword = bCryptPasswordEncoder.encode(registerDto.getPassword());
        LocalDateTime now = LocalDateTime.now();

        UserRole userRole = registerDto.getRole();

        if (!userRole.equals(UserRole.STUDENT)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Student is only created.");
        }

        boolean emailExists = studentRepository.existsByEmail(registerDto.getEmail());

        if (emailExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student already exists.");
        }

        List<Student> currentYearStudents = studentRepository.getCurrentYearStudents(registerDto.getJoiningYear());

        if (currentYearStudents.isEmpty()) {
            registerDto.setEnrollmentNumber(CommonUtil.generateEnrollmentId());
        } else {
            System.out.println("in else");
            registerDto.setEnrollmentNumber(CommonUtil.generateEnrollmentId(currentYearStudents.get(0).getEnrollmentNumber()));
        }

        User newUser = registerDto.getRole().createInstance();

        newUser.setName(registerDto.getName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(encodedPassword);
        newUser.setPhoneNumber(registerDto.getPhoneNumber());
        newUser.setRole(registerDto.getRole());
        newUser.setCreatedAt(registerDto.getCreatedAt());
        newUser.setUpdatedAt(registerDto.getUpdatedAt());

        if (newUser instanceof Student student) {
            student.setEnrollmentNumber(registerDto.getEnrollmentNumber());
            student.setJoiningYear(registerDto.getJoiningYear());
            student.setCurrentSemester(1);
        }

        return studentRepository.save((Student) newUser);
    }

    public Student getStudentById(String id) {
        Optional<Student> student = studentRepository.findById(id);

        if (student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        } else {
            return student.get();
        }
    }

    public List<StudentResponseDto> getStudentList() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("role").is(UserRole.STUDENT)),
                Aggregation.sort(Sort.Direction.ASC, "createdAt"),
                Aggregation.project(
                        "id",
                        "name",
                        "email",
                        "phoneNumber",
                        "enrollmentNumber",
                        "joiningYear",
                        "role",
                        "currentSemester",
                        "cgpa",
                        "createdAt",
                        "updatedAt"
                )
        );

        AggregationResults<StudentResponseDto> result = mongoTemplate.aggregate(
                aggregation,
                "users",
                StudentResponseDto.class
        );

        return result.getMappedResults();
    }

    public User updateStudent(String id, UpdateUserDto updateUserDto) {
        Optional<User> response = userRepository.findById(id);

        if (response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        Student student = (Student) response.get();

        student.setName(
                updateUserDto.getName() == null ? student.getName() : updateUserDto.getName()
        );
        student.setPhoneNumber(
                updateUserDto.getPhoneNumber() == null ? student.getPhoneNumber() : updateUserDto.getPhoneNumber()
        );
        student.setCurrentSemester(
                updateUserDto.getCurrentSemester() == 0 ? student.getCurrentSemester() : updateUserDto.getCurrentSemester()
        );

        return userRepository.save(student);
    }

    public void deleteStudentById(String id) {
        Optional<Student> student = studentRepository.findById(id);

        if (student.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
        }

        studentRepository.deleteById(id);
    }
}
