package com.abhay.assessment.java_assessment.admin.repository;

import com.abhay.assessment.java_assessment.admin.model.Admin;
import com.abhay.assessment.java_assessment.admin.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByEmail(String email);

    Optional<User> findByResetPasswordToken(String resetPasswordToken);
}