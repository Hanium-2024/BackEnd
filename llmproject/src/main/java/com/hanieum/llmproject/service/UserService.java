package com.hanieum.llmproject.service;

import com.hanieum.llmproject.config.jwt.JwtUtil;
import com.hanieum.llmproject.dto.*;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;

/**
 * 로그인과 로그아웃, 회원가입 기능을 구현한 서비스 클래스
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public TokenDto login(UserLoginRequest requestDto) {
        // LoginId와 Password를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), requestDto.getPassword());

        /*
         실제 유저 검증이 일어나는 부분
         CustomUserDetailsService의 loadUserByUsername 메소드가 실행
         */
        Authentication authentication;
        try {
            authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        } catch (BadCredentialsException e) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        System.out.println("auth name : " + authentication.getName());
        System.out.println("auth principal : " + authentication.getPrincipal());

        String loginId = getLoginIdFromAuthentication(authentication);

        TokenDto token = jwtUtil.createToken(loginId);

        return token;
    }

    public UserResponseDto signUp(UserSignupRequest requestDto) {
        checkDuplicatedLoginId(requestDto.getLoginId());
        checkDuplicatedEmail(requestDto.getEmail());

        return UserResponseDto.fromEntity(
                userRepository.save(
                        requestDto.toEntity(
                                passwordEncoder.encode(requestDto.getPassword())
                        )
                ));
    }

    private void checkDuplicatedLoginId(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.ID_DUPLICATED);
        }
    }

    private void checkDuplicatedEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
    }

    public TokenDto reissue(TokenRequest tokenRequest) {
        refreshTokenService.validateToken(tokenRequest.getLoginId(), tokenRequest.getRefreshToken());

        return jwtUtil.createToken(tokenRequest.getLoginId());
    }

    public void logout(String loginId) {
        refreshTokenService.deleteToken(loginId);
    }

    private String getLoginIdFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            User user = (User) principal;
            return user.getLoginId();
        }

        throw new NullPointerException();
    }
}


