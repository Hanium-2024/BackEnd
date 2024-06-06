package com.hanieum.llmproject.service;

import com.hanieum.llmproject.config.jwt.JwtUtil;
import com.hanieum.llmproject.dto.TokenDto;
import com.hanieum.llmproject.dto.UserLoginRequestDto;
import com.hanieum.llmproject.dto.UserResponseDto;
import com.hanieum.llmproject.dto.UserSignupRequestDto;
import com.hanieum.llmproject.exception.ErrorCode;
import com.hanieum.llmproject.exception.errortype.CustomException;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 로그인과 로그아웃, 회원가입 기능을 구현한 서비스 클래스
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public TokenDto login(UserLoginRequestDto requestDto) {
        // LoginId와 Password를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(requestDto.getLoginId(), requestDto.getPassword());

        /*
         실제 유저 검증이 일어나는 부분
         CustomUserDetailsService의 loadUserByUsername 메소드가 실행
         */
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);

        return jwtUtil.createToken(authentication);
    }

    @Override
    public UserResponseDto signUp(UserSignupRequestDto requestDto) {
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
            // Fixme Custom Exception 처리 필요
            throw new CustomException(ErrorCode.EMAIL_DUPLICATED);
        }
    }
}


