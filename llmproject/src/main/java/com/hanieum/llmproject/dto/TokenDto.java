package com.hanieum.llmproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenDto {
	private String grantType;
	private String accessToken;
	private String refreshToken;

	public static TokenDto buildToken(String accessToken, String refreshToken) {
		return TokenDto.builder()
			.grantType("Bearer ")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
