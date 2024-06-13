package com.hanieum.llmproject.controller;

import com.hanieum.llmproject.dto.*;

import com.hanieum.llmproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public Response<TokenDto> login(@RequestBody UserLoginRequest requestDto) {
        return Response.success("로그인 성공", userService.login(requestDto));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Response<UserResponseDto> signup(@Valid @RequestBody UserSignupRequest requestDto) {
        return Response.success("회원 가입 성공", userService.signUp(requestDto));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/reissue")
    public Response<TokenDto> reissue(@RequestBody TokenRequest tokenRequest) {
        return Response.success("토큰 재발급 성공", userService.reissue(tokenRequest));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/logout")
    public Response<Void> logout(@RequestBody LogoutRequest logoutRequest) {
        userService.logout(logoutRequest.getLoginId());
        return Response.success("로그아웃 성공", null);
    }

    @GetMapping("/test")
    public String test() {
        return "통과";
    }


}
