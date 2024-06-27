package com.hanieum.llmproject.service;

import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.RefreshToken;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 *  Refresh Token의 생성과 저장, 삭제 등을 책임지는 클래스
 *
 */
@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    @Value("#{'${jwt.refreshTokenValidTime}'.trim()}")
    private long refreshTokenValidTime;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(User user) {
        deleteTokenIfPresent(user);

        String newToken = UUID.randomUUID().toString();
        Date expirationDate = new Date(System.currentTimeMillis() + refreshTokenValidTime);

        RefreshToken refreshToken = RefreshToken.buildRefreshToken(
                newToken,
                user,
                expirationDate);

        saveRefreshToken(refreshToken);

        return newToken;
    }

    private void deleteTokenIfPresent(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(
                        token -> refreshTokenRepository.deleteByUser(user)
                );
    }

    private void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void validateToken(User user, String requestToken) {
        RefreshToken refreshToken = loadByUser(user);

        if (refreshToken.isExpiredToken()) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        if (!refreshToken.isSame(requestToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    private RefreshToken loadByUser(User user) {
        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTHENTICATION_FAILED));
    }

    public void deleteToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
