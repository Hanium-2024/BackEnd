package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.UserLoginRequestDto;
import com.hanieum.llmproject.dto.UserLoginResponseDto;
import com.hanieum.llmproject.dto.UserSignupRequestDto;
import com.hanieum.llmproject.dto.UserSignupResponseDto;
import com.hanieum.llmproject.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        return ResponseEntity.ok(userService.login(requestDto));
    }

    @PostMapping("/signup")
    public Long signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        return userService.signUp(requestDto);
    }




}
