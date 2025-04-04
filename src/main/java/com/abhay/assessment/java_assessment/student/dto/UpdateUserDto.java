package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

@Data
public class UpdateUserDto {
    private String name;
    private String phoneNumber;
    private int currentSemester;
}
