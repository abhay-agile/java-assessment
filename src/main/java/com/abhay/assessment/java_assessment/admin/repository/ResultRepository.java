package com.abhay.assessment.java_assessment.admin.repository;

import com.abhay.assessment.java_assessment.admin.model.Result;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResultRepository extends MongoRepository<Result, String> {
}
