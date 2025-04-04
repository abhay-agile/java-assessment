package com.abhay.assessment.java_assessment.admin.repository;

import com.abhay.assessment.java_assessment.admin.model.Student;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    List<Student> findAllByCurrentSemester(int currentSemester);

    boolean existsByEmail(String email);
    boolean existsByIdAndCurrentSemester(String id, int currentSemester);

    @Aggregation(pipeline = {
            "{ '$match': { 'joiningYear': ?0 } }",
            "{ '$sort': { 'createdAt': -1 } }"
    })
    List<Student> getCurrentYearStudents(int year);
}