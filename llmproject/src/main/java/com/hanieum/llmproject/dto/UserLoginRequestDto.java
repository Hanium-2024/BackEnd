package com.hanieum.llmproject.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginRequestDto {
    private String loginId;
    private String password;
}
