package com.abhay.assessment.java_assessment.admin.dto;

import com.abhay.assessment.java_assessment.admin.model.Admin;
import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class ResponseRegisterDto {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    private UserRole role;

    private Instant createdAt;
    private Instant updatedAt;

    private boolean active;

    private String enrollmentNumber;
    private int joiningYear;
    private int currentSemester;

    public static ResponseRegisterDto fromUser(User user) {
        ResponseRegisterDto resDto = new ResponseRegisterDto();

        resDto.setName(user.getName());
        resDto.setEmail(user.getEmail());
        resDto.setPhoneNumber(user.getPhoneNumber());
        resDto.setPassword(user.getPassword());
        resDto.setRole(user.getRole());
        resDto.setCreatedAt(user.getCreatedAt());
        resDto.setUpdatedAt(user.getUpdatedAt());

        if (user instanceof Admin admin) {
            resDto.setActive(admin.getActive());
        }

        if (user instanceof Student student) {
            resDto.setEnrollmentNumber(student.getEnrollmentNumber());
            resDto.setJoiningYear(student.getJoiningYear());
            resDto.setCurrentSemester(student.getCurrentSemester());
        }

        return resDto;
    }
}
