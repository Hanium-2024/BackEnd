package com.hanieum.llmproject.service;

import com.hanieum.llmproject.dto.TokenDto;
import com.hanieum.llmproject.dto.UserLoginRequestDto;
import com.hanieum.llmproject.dto.UserResponseDto;
import com.hanieum.llmproject.dto.UserSignupRequestDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

    TokenDto login(UserLoginRequestDto requestDto);

    UserResponseDto signUp(UserSignupRequestDto requestDto);
}

