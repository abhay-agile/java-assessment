package com.abhay.assessment.java_assessment.student.repository;

import com.abhay.assessment.java_assessment.student.model.Subject;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends MongoRepository<Subject, String> {

    Optional<Subject> findByName(String name);

    @Aggregation(pipeline = {
            "{ '$match': { 'semester': ?0 } }",
            "{ '$sort': { 'semester': 1 } }"
    })
    List<Subject> findAllBySemester(int semester);
}
