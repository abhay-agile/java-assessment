package com.abhay.assessment.java_assessment.student.controller;


import com.abhay.assessment.java_assessment.admin.dto.RequestRegisterDto;
import com.abhay.assessment.java_assessment.admin.dto.ResponseRegisterDto;
import com.abhay.assessment.java_assessment.admin.model.Student;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import com.abhay.assessment.java_assessment.student.dto.StudentResponseDto;
import com.abhay.assessment.java_assessment.student.dto.UpdateUserDto;
import com.abhay.assessment.java_assessment.student.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create-student")
    public ResponseEntity<ApiResponse<ResponseRegisterDto>> createStudent(
            @RequestBody RequestRegisterDto registerDto
    ) {
        if (registerDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (registerDto.getRole() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            User registeredUser = studentService.createStudent(registerDto);

            if (registeredUser == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                ApiResponse.failure(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Student could not be registered."
                                )
                        );
            }

            ResponseRegisterDto responseDto = ResponseRegisterDto.fromUser(registeredUser);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.CREATED,
                            "Student created successfully.",
                            responseDto
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
    @GetMapping("/get-student/{id}")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getStudentById(@PathVariable String id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Student studentById = studentService.getStudentById(id);

            if (studentById == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                ApiResponse.failure(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Student could not be found."
                                )
                        );
            }

            StudentResponseDto responseDto = StudentResponseDto.fromStudent(studentById);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student details fetched successfully.",
                            responseDto
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
    @GetMapping("/student-list")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> studentsList() {
        try {
            List<StudentResponseDto> studentList = studentService.getStudentList();

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student list fetched successfully.",
                            studentList
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
    @PatchMapping("/update-student/{id}")
    public ResponseEntity<ApiResponse<?>> updateStudent(
            @PathVariable String id,
            @RequestBody UpdateUserDto updateUserDto
    ) {
        if (updateUserDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            User updatedStudent = studentService.updateStudent(id, updateUserDto);

            StudentResponseDto responseDto = StudentResponseDto.fromStudent((Student) updatedStudent);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student updated successfully.",
                            responseDto
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
    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<ApiResponse<?>> deleteStudentById(@PathVariable String id) {
        if (id == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            studentService.deleteStudentById(id);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Student deleted successfully."
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
