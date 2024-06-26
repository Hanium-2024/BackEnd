package com.hanieum.llmproject.config.jwt;

import com.hanieum.llmproject.dto.TokenDto;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Component
public class JwtUtil implements Serializable {

    private static final String BEARER_PREFIX = "Bearer ";

    private final SecretKey key;

    private final long accessTokenValidTime;
    private final RefreshTokenService refreshTokenService;

    public JwtUtil(
            @Value("#{'${jwt.secretKey}'.trim()}") String key,
            @Value("#{'${jwt.accessTokenValidTime}'.trim()}") String ATKValidTime,
            RefreshTokenService refreshTokenService
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(key));
        this.accessTokenValidTime = Long.parseLong(ATKValidTime);
        this.refreshTokenService = refreshTokenService;
    }

    public TokenDto createToken(User user) {

        String accessToken = createAccessToken(user.getLoginId());
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return TokenDto.buildToken(accessToken, refreshToken);
    }

    private String createAccessToken(String loginId) {
        return Jwts.builder()
                .claim("loginId", loginId)
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(key)
                .compact();
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null) {
            throw new CustomException(ErrorCode.TOKEN_NOT_FOUND);
        }

        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }

        throw new MalformedJwtException(ErrorCode.INVALID_TOKEN.getMessage());
    }

    public void validateAccessToken(String token) {
        Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();

        String username = claims.get("loginId", String.class);

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }


}
