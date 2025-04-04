package com.abhay.assessment.java_assessment.admin.dto;

import com.abhay.assessment.java_assessment.admin.model.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestRegisterDto {

    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    private UserRole role;

    private Instant createdAt;
    private Instant updatedAt;

    private boolean active = true;

    private String enrollmentNumber;
    private int joiningYear;
    private int currentSemester;
}
