package com.abhay.assessment.java_assessment.student.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "attendance")
@TypeAlias("attendance")
public class Attendance {

    @Id
    private String id;

    private ObjectId studentId;
    private ObjectId subjectId;
    private boolean attendLecture;
    private Date dateOfLecture;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

    public Attendance(
            ObjectId studentId,
            ObjectId subjectId,
            boolean attendLecture,
            Date dateOfLecture
    ) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.attendLecture = attendLecture;
        this.dateOfLecture = dateOfLecture;
    }

}
