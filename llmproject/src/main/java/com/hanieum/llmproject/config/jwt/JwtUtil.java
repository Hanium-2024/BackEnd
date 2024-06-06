package com.hanieum.llmproject.config.jwt;

import com.hanieum.llmproject.dto.TokenDto;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import io.jsonwebtoken.*;
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


    private final SecretKey key;
    private final long accessTokenValidTime;
    private final long refreshTokenValidTime;

    public JwtUtil(@Value("#{'${jwt.secretKey}'.trim()}") String secret_key,
                   @Value("#{'${jwt.accessTokenValidTime}'.trim()}") String accessTokenValidTime,
                   @Value("#{'${jwt.refreshTokenValidTime}'.trim()}") String refreshTokenValidTime) {
        log.info("secret_key is" + secret_key);
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret_key));
        this.accessTokenValidTime = Long.parseLong(accessTokenValidTime);
        this.refreshTokenValidTime = Long.parseLong(refreshTokenValidTime);
    }

    public TokenDto createToken(Authentication authentication) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .claim("loginId", authentication.getName())
                .issuedAt(now)
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidTime))
                .signWith(key)
                .compact();

        return TokenDto.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring("Bearer ".length());
        } else {
            throw new MalformedJwtException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(key).build()
                .parseSignedClaims(token);
        return true;

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
