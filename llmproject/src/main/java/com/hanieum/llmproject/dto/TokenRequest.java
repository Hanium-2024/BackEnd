package com.hanieum.llmproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AccessToken 재발급 요청 시
 * RequestBody에 담을 LoginId 와 RefreshToken 필드를 가진 Dto 객체
 */
@Getter
@NoArgsConstructor
public class TokenRequest {
	private String loginId;
	private String refreshToken;
}
