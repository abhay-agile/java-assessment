package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SubmitAttendanceRequestDto {

    private int semester;
    private LocalDate lectureDate;
    private String subjectId;
    private List<String> presentStudents;
}
