package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentGradePointResponseDto {

    private String studentName;
    private String studentEmail;
    private String studentEnrollmentNumber;
    private float cgpa;
    private List<SemesterWiseGradePointDto> result;
}
