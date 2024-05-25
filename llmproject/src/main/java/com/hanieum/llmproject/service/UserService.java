package com.hanieum.llmproject.service;

import com.hanieum.llmproject.dto.UserLoginRequestDto;
import com.hanieum.llmproject.dto.UserLoginResponseDto;
import com.hanieum.llmproject.model.User;

public interface UserService {

    // Fixme 매개변수와 리턴 타입 Dto로 변환 필요
    User join(String username, String password);
    UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto);
}

