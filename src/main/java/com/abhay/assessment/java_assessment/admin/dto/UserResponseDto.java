package com.abhay.assessment.java_assessment.admin.dto;

import com.abhay.assessment.java_assessment.admin.model.Admin;
import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponseDto {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private boolean isActive;
    private String enrollmentNumber;
    private int joiningYear;
    private int currentSemester;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserResponseDto fromUser(User user) {
        UserResponseDto responseDto = new UserResponseDto();

        responseDto.setId(user.getId());
        responseDto.setName(user.getName());
        responseDto.setEmail(user.getEmail());
        responseDto.setPhoneNumber(user.getPhoneNumber());
        responseDto.setRole(user.getRole());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());

        if (user instanceof Admin admin) {
            responseDto.setActive(admin.getActive());
        }

        if (user instanceof Student student) {
            responseDto.setEnrollmentNumber(student.getEnrollmentNumber());
            responseDto.setJoiningYear(student.getJoiningYear());
            responseDto.setCurrentSemester(student.getCurrentSemester());
        }

        return responseDto;
    }
}
