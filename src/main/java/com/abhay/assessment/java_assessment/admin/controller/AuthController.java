package com.abhay.assessment.java_assessment.admin.controller;

import com.abhay.assessment.java_assessment.admin.dto.LoginResponseDto;
import com.abhay.assessment.java_assessment.admin.dto.RequestRegisterDto;
import com.abhay.assessment.java_assessment.admin.dto.ResponseRegisterDto;
import com.abhay.assessment.java_assessment.admin.model.User;
import com.abhay.assessment.java_assessment.admin.service.AuthService;
import com.abhay.assessment.java_assessment.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.AccountNotFoundException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register-admin", produces = "application/json")
    public ResponseEntity<ApiResponse<ResponseRegisterDto>> registerAdmin(
            @RequestBody RequestRegisterDto registerDto
    ) {
        if (registerDto == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (registerDto.getRole() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            User registeredUser = authService.registerUser(registerDto);

            if (registeredUser == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(
                                ApiResponse.failure(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Admin could not be registered."
                                )
                        );
            }

            ResponseRegisterDto responseDto = ResponseRegisterDto.fromUser(registeredUser);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.CREATED,
                            "Admin created successfully.",
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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(
            @RequestBody Map<String, String> body
    ) {
        try {
            String userId = body.get("userId");
            String password = body.get("password");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(HttpStatus.NOT_FOUND, "Please enter user id."));
            }

            if (password.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(HttpStatus.NOT_FOUND, "Please enter password."));
            }

            LoginResponseDto response = authService.login(userId, password);

            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.failure(HttpStatus.UNAUTHORIZED));
            } else {
                return ResponseEntity.ok(
                        ApiResponse.success(
                                HttpStatus.OK,
                                "User logged in successfully.",
                                response
                        )
                );
            }
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");

            if (email.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.failure(HttpStatus.NOT_FOUND, "Email not found."));
            }

            String resetToken = authService.forgotPassword(email);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            HttpStatus.OK,
                            "Reset password token generated successfully.",
                            resetToken
                    )
            );
        } catch (AccountNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword (
            @RequestBody Map<String, String> body
    ) {
        try {
            String resetPasswordToken = body.get("resetPasswordToken");
            String newPassword = body.get("newPassword");

            authService.resetPassword(resetPasswordToken, newPassword);

            return ResponseEntity.ok(
                    ApiResponse.success(HttpStatus.OK, "Password reset successfully.")
            );
        } catch (AccountNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred."));
        }
    }
}
