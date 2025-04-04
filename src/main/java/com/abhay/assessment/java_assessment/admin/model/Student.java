package com.abhay.assessment.java_assessment.admin.model;


import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
@TypeAlias("student")
public class Student extends User {
    private String enrollmentNumber;
    private int joiningYear;
    private int currentSemester;

    @Builder
    public Student(
            String name,
            String email,
            String phoneNumber,
            String password,
            UserRole role,
            Instant createdAt,
            Instant updatedAt,
            String enrollmentNumber,
            int joiningYear,
            int currentSemester
    ) {
        super(name, email, phoneNumber, password, role, createdAt, updatedAt);
        this.enrollmentNumber = enrollmentNumber;
        this.joiningYear = joiningYear;
        this.currentSemester = currentSemester;
    }
}
