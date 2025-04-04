package com.abhay.assessment.java_assessment.student.repository;

import com.abhay.assessment.java_assessment.student.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    @Query("{'subjectId': ?0, 'dateOfLecture': { $gte: ?1, $lt: ?2 }}")
    Optional<Attendance> findBySubjectIdAndDateOfLecture(String subjectId, Date startDate, Date endDate);
}
