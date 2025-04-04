package com.abhay.assessment.java_assessment.student.dto;

import lombok.Data;

@Data
public class CreateOrUpdateSubjectDto {

    private String name;
    private int semester;
    private int creditPoint;
}
