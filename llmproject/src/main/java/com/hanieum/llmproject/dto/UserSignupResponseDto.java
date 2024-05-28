package com.hanieum.llmproject.dto;

import com.hanieum.llmproject.model.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {
    private Long userNo;
    private String userId;
    private String password;
    private String username;
    private String email;

    public UserSignupResponseDto(User user) {
        this.userNo = user.getUserNo();
        this.userId = user.getUserId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
