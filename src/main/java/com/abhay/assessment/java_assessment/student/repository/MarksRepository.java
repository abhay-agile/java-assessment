package com.abhay.assessment.java_assessment.student.repository;

import com.abhay.assessment.java_assessment.student.model.Marks;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MarksRepository extends MongoRepository<Marks, String> {
    List<Marks> findByStudentIdAndSubjectId(ObjectId studentId, ObjectId subjectId);
}
