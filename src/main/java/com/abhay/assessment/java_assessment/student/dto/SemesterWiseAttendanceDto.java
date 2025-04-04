package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class SemesterWiseAttendanceDto {

    private int semester;
    private int totalAttendLecture;
    private int totalLecture;
    private float attendance;
    private List<SubjectWiseAttendanceDto> subject;
}
