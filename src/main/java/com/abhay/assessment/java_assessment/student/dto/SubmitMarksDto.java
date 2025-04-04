package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

@Data
public class SubmitMarksDto {

    private String studentId;
    private String subjectId;
    private int semester;
    private int obtainedMarks;

}
