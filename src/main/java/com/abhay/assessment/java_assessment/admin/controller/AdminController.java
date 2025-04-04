package com.abhay.assessment.java_assessment.admin.controller;

import com.abhay.assessment.java_assessment.admin.dto.UserResponseDto;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.service.AdminService;
import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import com.abhay.assessment.java_assessment.student.dto.UpdateUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-list")
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> findAllUsers() {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Admin list fetched successfully.",
                            adminService.findAllUsers()
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
    @GetMapping("logged-in-user")
    public ResponseEntity<ApiResponse<UserResponseDto>> getLoggedInUser() {
        try {
            User loggedInUser = adminService.getLoggedInUser();

            UserResponseDto response = UserResponseDto.fromUser(loggedInUser);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Logged in user details fetched successfully.",
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
    @PatchMapping("update-logged-in-user")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateLoggedInUser(
            @RequestBody UpdateUserDto updateUserDto
    ) {
        try {
            User loggedInUser = adminService.updateLoggedInUser(updateUserDto);

            UserResponseDto response = UserResponseDto.fromUser(loggedInUser);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Logged in user details fetched successfully.",
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
    @PostMapping("update-password")
    public ResponseEntity<ApiResponse<?>> updateLoggedInUserPassword(
            @RequestBody Map<String, String> body
    ) {
        try {
            String oldPassword = body.get("oldPassword");
            String newPassword = body.get("newPassword");

            if (oldPassword == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(HttpStatus.NOT_FOUND, "Please enter old password."));
            }

            if (newPassword.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(HttpStatus.NOT_FOUND, "Please enter new password."));
            }

            adminService.updateLoggedInUserPassword(oldPassword, newPassword);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Logged in user password updated successfully."
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
