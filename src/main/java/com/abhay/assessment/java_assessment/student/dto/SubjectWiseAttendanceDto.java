package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

@Data
public class SubjectWiseAttendanceDto {

    private String subjectName;
    private int attendLecture;
    private int totalLecture;
    private float attendance;
}
