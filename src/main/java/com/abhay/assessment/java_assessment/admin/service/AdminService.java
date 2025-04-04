package com.abhay.assessment.java_assessment.admin.service;

import com.abhay.assessment.java_assessment.admin.dto.UserResponseDto;
import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import com.abhay.assessment.java_assessment.admin.repository.StudentRepository;
import com.abhay.assessment.java_assessment.admin.repository.UserRepository;
import com.abhay.assessment.java_assessment.common.util.CommonUtil;
import com.abhay.assessment.java_assessment.student.dto.UpdateUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AdminService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final MongoTemplate mongoTemplate;

    public AdminService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            MongoTemplate mongoTemplate
    ) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<UserResponseDto> findAllUsers() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("role").is(UserRole.ADMIN)),
                Aggregation.sort(Sort.Direction.ASC, "createdAt"),
                Aggregation.project(
                        "id",
                        "name",
                        "email",
                        "phoneNumber",
                        "isActive",
                        "role",
                        "createdAt",
                        "updatedAt"
                )
        );

        AggregationResults<UserResponseDto> result = mongoTemplate.aggregate(
                aggregation,
                "users",
                UserResponseDto.class
        );

        return result.getMappedResults();
    }

    public User getLoggedInUser() {
        String loggedInUserEmail = CommonUtil.getLoggedInUserEmail();

        Optional<User> loggedInUser = userRepository.findByEmail(loggedInUserEmail);

        if (loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        } else {
            return loggedInUser.get();
        }
    }

    public User updateLoggedInUser(UpdateUserDto updateUserDto) {
        String loggedInUserEmail = CommonUtil.getLoggedInUserEmail();

        Optional<? extends User> loggedInUser = userRepository.findByEmail(loggedInUserEmail);

        if (loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        loggedInUser.get().setName(
                updateUserDto.getName() == null ? loggedInUser.get().getName() : updateUserDto.getName()
        );
        loggedInUser.get().setPhoneNumber(
                updateUserDto.getPhoneNumber() == null ? loggedInUser.get().getPhoneNumber() : updateUserDto.getPhoneNumber()
        );

        return userRepository.save(loggedInUser.get());
    }

    public void updateLoggedInUserPassword(String oldPassword, String newPassword) {
        String loggedInUserEmail = CommonUtil.getLoggedInUserEmail();

        Optional<User> loggedInUser = userRepository.findByEmail(loggedInUserEmail);

        if (loggedInUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        if (!bCryptPasswordEncoder.matches(oldPassword, loggedInUser.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Old password does not match.");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);

        loggedInUser.get().setPassword(encodedPassword);

        userRepository.save(loggedInUser.get());
    }
}
