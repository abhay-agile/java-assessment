package com.abhay.assessment.java_assessment.student.dto;

import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.UserRole;
import lombok.Data;

import java.time.Instant;

@Data
public class StudentResponseDto {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String enrollmentNumber;
    private int joiningYear;
    private int currentSemester;
    private Instant createdAt;
    private Instant updatedAt;

    public static StudentResponseDto fromStudent(Student student) {
        StudentResponseDto responseDto = new StudentResponseDto();

        responseDto.setId(student.getId());
        responseDto.setName(student.getName());
        responseDto.setEmail(student.getEmail());
        responseDto.setPhoneNumber(student.getPhoneNumber());
        responseDto.setRole(student.getRole());
        responseDto.setEnrollmentNumber(student.getEnrollmentNumber());
        responseDto.setJoiningYear(student.getJoiningYear());
        responseDto.setCurrentSemester(student.getCurrentSemester());
        responseDto.setCreatedAt(student.getCreatedAt());
        responseDto.setUpdatedAt(student.getUpdatedAt());

        return responseDto;
    }
}
