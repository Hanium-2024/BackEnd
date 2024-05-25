package com.hanieum.llmproject.config.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class JwtUtil {

    @Value("{jwt.secret_key}")
    private String secret_key;

    // Todo Jwt 토큰 생성 로직 구현 필요
    public String generateToken(String userId) {
        return "";
    }
}
