package com.abhay.assessment.java_assessment.student.repository;

import com.abhay.assessment.java_assessment.student.model.GradePoint;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GradePointRepository extends MongoRepository<GradePoint, String> {
}
