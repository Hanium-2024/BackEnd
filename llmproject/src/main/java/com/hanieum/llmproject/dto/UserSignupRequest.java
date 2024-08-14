package com.hanieum.llmproject.dto;

import com.hanieum.llmproject.model.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignupRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	private String loginId;
	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;
	@NotBlank(message = "이름을 입력해주세요.")
	private String username;
	@NotBlank(message = "이메일을 입력해주세요.")
	private String email;

	@Builder
	public User toEntity(String password) {
		return User.builder()
			.loginId(this.loginId)
			.password(password)
			.username(this.username)
			.email(this.email)
			.build();
	}
}
