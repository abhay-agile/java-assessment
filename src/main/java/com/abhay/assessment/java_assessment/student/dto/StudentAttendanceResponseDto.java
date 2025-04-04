package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentAttendanceResponseDto {

    private String studentId;
    private String studentName;
    private String studentEmail;
    private String studentEnrollmentNumber;
    private List<SemesterWiseAttendanceDto> attendance;
}
