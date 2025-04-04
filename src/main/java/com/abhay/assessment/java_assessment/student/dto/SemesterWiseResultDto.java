package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

import java.util.List;

@Data
public class SemesterWiseResultDto {

    private int semester;
    private List<SubjectWiseResultDto> marks;
}
