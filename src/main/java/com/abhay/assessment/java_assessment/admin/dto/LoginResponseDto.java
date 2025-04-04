package com.abhay.assessment.java_assessment.admin.dto;

import com.abhay.assessment.java_assessment.admin.model.User;
import lombok.Getter;

public class LoginResponseDto {

    private User user;
    @Getter
    private String token;

    public LoginResponseDto(User user, String token) {
        this.user = user;
        this.token = token;
    }

}
