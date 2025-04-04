package com.abhay.assessment.java_assessment.student.dto;

import com.abhay.assessment.java_assessment.student.model.ResultStatus;
import lombok.Data;

@Data
public class SubjectWiseResultDto {

    private String subjectName;
    private int subjectCredit;
    private float subjectGradePoint;
    private float overAllGradePoint;
    private ResultStatus subjectStatus;
}
