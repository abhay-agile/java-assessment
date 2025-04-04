package com.abhay.assessment.java_assessment.student.service;

import com.abhay.assessment.java_assessment.student.dto.CreateOrUpdateSubjectDto;
import com.abhay.assessment.java_assessment.student.model.Subject;
import com.abhay.assessment.java_assessment.student.repository.SubjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        super();
        this.subjectRepository = subjectRepository;
    }

    public Subject createSubject(CreateOrUpdateSubjectDto subjectDto) {
        Optional<Subject> exist = subjectRepository.findByName(subjectDto.getName());

        if (exist.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject is already exists.");
        }

        Subject subject = new Subject();
        subject.setName(subjectDto.getName());
        subject.setSemester(subjectDto.getSemester());
        subject.setCreditPoint(subjectDto.getCreditPoint());

        return subjectRepository.save(subject);
    }

    public Subject getSubjectById(String id) {
        Optional<Subject> subject = subjectRepository.findById(id);

        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        } else {
            return subject.get();
        }
    }

    public List<Subject> subjectList() {
        return subjectRepository.findAll();
    }

    public List<Subject> subjectListBySemester(int semester) {
        return subjectRepository.findAllBySemester(semester);
    }

    public Subject updateSubject(String id, CreateOrUpdateSubjectDto subjectDto) {
        Optional<Subject> subject = subjectRepository.findById(id);

        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        if (!subject.get().getName().equals(subjectDto.getName())) {

            Optional<Subject> exist = subjectRepository.findByName(subjectDto.getName());

            if (exist.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject is already exists.");
            }
        }

        subject.get().setName(subjectDto.getName());
        subject.get().setSemester(subjectDto.getSemester());
        subject.get().setCreditPoint(subjectDto.getCreditPoint());

        return subjectRepository.save(subject.get());
    }

    public void deleteSubject(String id) {
        Optional<Subject> subject = subjectRepository.findById(id);

        if (subject.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found.");
        }

        subjectRepository.deleteById(id);
    }
}
