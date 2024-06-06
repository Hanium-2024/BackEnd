package com.hanieum.llmproject.dto;

import com.hanieum.llmproject.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private String loginId;
    private String username;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .loginId(user.getLoginId())
                .username(user.getUsername())
                .build();
    }
}
