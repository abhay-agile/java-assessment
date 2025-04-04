package com.abhay.assessment.java_assessment.student.controller;

import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import com.abhay.assessment.java_assessment.student.dto.StudentAttendanceResponseDto;
import com.abhay.assessment.java_assessment.student.dto.SubmitAttendanceRequestDto;
import com.abhay.assessment.java_assessment.student.model.Attendance;
import com.abhay.assessment.java_assessment.student.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/submit-attendance")
    public ResponseEntity<ApiResponse<?>> submitAttendance(@RequestBody SubmitAttendanceRequestDto submitAttendanceRequestDto) {
        try {
            if (submitAttendanceRequestDto == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            attendanceService.submitAttendance(submitAttendanceRequestDto);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Attendance submitted successfully for the day."
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
    @GetMapping("/get-student-attendance/{studentId}")
    public ResponseEntity<ApiResponse<StudentAttendanceResponseDto>> getStudentAttendance(@PathVariable String studentId) {
        try {
            if (studentId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentAttendanceResponseDto> response = attendanceService.getStudentAttendance(studentId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student's attendance fetched successfully.",
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
    @GetMapping("/get-attendance")
    public ResponseEntity<ApiResponse<StudentAttendanceResponseDto>> getLoggedInStudentAttendance() {
        try {
            String userId = attendanceService.getLoggedInUserId();

            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            List<StudentAttendanceResponseDto> response = attendanceService.getStudentAttendance(userId);

            if (response.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No attendance to show.");
            }

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Attendance fetched successfully.",
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
    @GetMapping("/get-all-student-attendance")
    public ResponseEntity<ApiResponse<List<StudentAttendanceResponseDto>>> getAllStudentAttendance() {
        try {
            List<StudentAttendanceResponseDto> response = attendanceService.getAllStudentAttendance();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Attendance list fetched successfully.",
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
