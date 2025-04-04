package com.abhay.assessment.java_assessment.admin.repository;

import com.abhay.assessment.java_assessment.admin.dto.UserResponseDto;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByResetPasswordToken(String resetPasswordToken);

    List<UserResponseDto> findAllByRole(UserRole role);
}
