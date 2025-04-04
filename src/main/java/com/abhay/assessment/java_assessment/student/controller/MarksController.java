package com.abhay.assessment.java_assessment.student.controller;

import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import com.abhay.assessment.java_assessment.student.dto.StudentGradePointResponseDto;
import com.abhay.assessment.java_assessment.student.dto.StudentMarksResponseDto;
import com.abhay.assessment.java_assessment.student.dto.SubmitMarksDto;
import com.abhay.assessment.java_assessment.student.model.Marks;
import com.abhay.assessment.java_assessment.student.service.MarksService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/marks")
public class MarksController {

    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/submit-marks")
    public ResponseEntity<ApiResponse<Marks>> submitMarks(@RequestBody SubmitMarksDto submitMarksDto) {
        try {
            if (submitMarksDto == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Marks response = marksService.submitMarks(submitMarksDto);

            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                ApiResponse.failure(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Marks could not be submitted."
                                )
                        );
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.CREATED,
                            "Marks submitted successfully.",
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/student-marks/{studentId}")
    public ResponseEntity<ApiResponse<StudentMarksResponseDto>> viewSemesterWiseStudentMarks(
            @PathVariable String studentId
    ) {
        try {
            if (studentId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentMarksResponseDto> response = marksService.viewSemesterWiseStudentMarks(studentId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student's marks fetched successfully.",
                            response.get(0)
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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/get-marks")
    public ResponseEntity<ApiResponse<StudentMarksResponseDto>> viewLoggedInStudentMarks() {
        try {
            String userId = marksService.getLoggedInUserId();

            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentMarksResponseDto> response = marksService.viewSemesterWiseStudentMarks(userId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No marks to show.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Marks fetched successfully.",
                            response.get(0)
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
    @GetMapping("/all-student-marks")
    public ResponseEntity<ApiResponse<List<StudentMarksResponseDto>>> viewAllStudentMarks() {
        try {
            List<StudentMarksResponseDto> response = marksService.viewAllStudentMarks();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Marks list fetched successfully.",
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/student-grade-point/{studentId}")
    public ResponseEntity<ApiResponse<StudentGradePointResponseDto>> getStudentGradePoint(
            @PathVariable String studentId
    ) {
        try {
            if (studentId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentGradePointResponseDto> response = marksService.getStudentGradePoint(studentId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student's grade point result fetched successfully.",
                            response.get(0)
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

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/get-grade-point")
    public ResponseEntity<ApiResponse<StudentGradePointResponseDto>> getLoggedInStudentGradePoint() {
        try {
            String userId = marksService.getLoggedInUserId();

            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentGradePointResponseDto> response = marksService.getStudentGradePoint(userId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No grade point to show.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Grade point result fetched successfully.",
                            response.get(0)
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
    @GetMapping("/all-student-grade-point")
    public ResponseEntity<ApiResponse<List<StudentGradePointResponseDto>>> getAllStudentGradePoint() {
        try {
            List<StudentGradePointResponseDto> response = marksService.getAllStudentGradePoint();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Grade point list fetched successfully.",
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
}
