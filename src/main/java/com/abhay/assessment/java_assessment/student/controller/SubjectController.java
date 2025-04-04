package com.abhay.assessment.java_assessment.student.controller;

import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import com.abhay.assessment.java_assessment.student.dto.CreateOrUpdateSubjectDto;
import com.abhay.assessment.java_assessment.student.model.Subject;
import com.abhay.assessment.java_assessment.student.service.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-subject")
    public ResponseEntity<ApiResponse<Subject>> createSubject(
            @RequestBody CreateOrUpdateSubjectDto subjectDto
    ) {
        try {
            if (subjectDto == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Subject response = subjectService.createSubject(subjectDto);

            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                ApiResponse.failure(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Subject could not be created."
                                )
                        );
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.CREATED,
                            "Subject created successfully.",
                            response
                    )
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/get-subject/{id}")
    public ResponseEntity<ApiResponse<Subject>> getSubjectById(@PathVariable String id) {
        try {
            if (id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Subject subjectById = subjectService.getSubjectById(id);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Subject details fetched successfully.",
                            subjectById
                    )
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/subject-list")
    public ResponseEntity<ApiResponse<List<Subject>>> subjectList() {
        try {
            List<Subject> subjects = subjectService.subjectList();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Subject list fetched successfully.",
                            subjects
                    )
            );

        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/get-subject-by-semester/{semester}")
    public ResponseEntity<ApiResponse<List<Subject>>> subjectListBySemester(@PathVariable int semester) {
        try {
            List<Subject> subjects = subjectService.subjectListBySemester(semester);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Semester wise subject list fetched successfully.",
                            subjects
                    )
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update-subject/{id}")
    public ResponseEntity<ApiResponse<Subject>> updateSubject(
            @PathVariable String id,
            @RequestBody CreateOrUpdateSubjectDto subjectDto
    ) {
        try {
            if (subjectDto == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Subject updatedSubject = subjectService.updateSubject(id, subjectDto);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Subject updated successfully.",
                            updatedSubject
                    )
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete-subject/{id}")
    public ResponseEntity<ApiResponse<?>> deleteSubject(@PathVariable String id) {
        try {
            if (id == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            subjectService.deleteSubject(id);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Subject deleted successfully."
                    )
            );
        } catch (ResponseStatusException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(ApiResponse.failure(e.getStatusCode(), e.getReason()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

}
