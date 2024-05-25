package com.hanieum.llmproject.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 요청에 대한 응답 dto
 *
 * message: 요청에 대한 성공, 실패 시 담을 메시지
 * token: 로그인 성공 시 Jwt 토큰 값, 실패 시 null로 담기
 */
@Getter
@Builder
public class UserLoginResponseDto {
    private String message;
    private String token;
}
