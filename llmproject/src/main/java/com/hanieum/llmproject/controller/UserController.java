package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.*;
import com.hanieum.llmproject.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public Response<TokenDto> login(@RequestBody UserLoginRequestDto requestDto) {
        return new Response<>("true", "로그인 성공", userService.login(requestDto));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Response<UserResponseDto> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        return new Response<>("true", "회원 가입 성공", userService.signUp(requestDto));
    }

    @GetMapping("/test")
    public String test() {
        return "통과";
    }


}
