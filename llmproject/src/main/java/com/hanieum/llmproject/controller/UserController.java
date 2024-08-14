package com.hanieum.llmproject.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hanieum.llmproject.dto.LogoutRequest;
import com.hanieum.llmproject.dto.Response;
import com.hanieum.llmproject.dto.TokenDto;
import com.hanieum.llmproject.dto.TokenRequest;
import com.hanieum.llmproject.dto.UserLoginRequest;
import com.hanieum.llmproject.dto.UserResponseDto;
import com.hanieum.llmproject.dto.UserSignupRequest;
import com.hanieum.llmproject.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
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
