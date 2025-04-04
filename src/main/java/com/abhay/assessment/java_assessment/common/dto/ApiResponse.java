package com.abhay.assessment.java_assessment.common.dto;

import lombok.Data;
import org.springframework.http.HttpStatusCode;

@Data
public class ApiResponse<T> {

    private HttpStatusCode status;
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(HttpStatusCode status, boolean success, String message, T data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(HttpStatusCode status, String message, T data) {
        return new ApiResponse<>(status, true, message, data);
    }

    public static <T> ApiResponse<T> success(HttpStatusCode status, String message) {
        return new ApiResponse<>(status, true, message, null);
    }

    public static <T> ApiResponse<T> success(HttpStatusCode status) {
        return new ApiResponse<>(status, true, "Success.", null);
    }

    public static <T> ApiResponse<T> failure(HttpStatusCode status) {
        return new ApiResponse<>(status, false, "Error.", null);
    }

    public static <T> ApiResponse<T> failure(HttpStatusCode status, String message) {
        return new ApiResponse<>(status, false, message, null);
    }
}
