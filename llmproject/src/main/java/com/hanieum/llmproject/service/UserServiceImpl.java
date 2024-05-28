package com.hanieum.llmproject.service;

import com.hanieum.llmproject.dto.UserLoginRequestDto;
import com.hanieum.llmproject.dto.UserLoginResponseDto;
import com.hanieum.llmproject.dto.UserSignupRequestDto;
import com.hanieum.llmproject.dto.UserSignupResponseDto;
import com.hanieum.llmproject.model.User;
import com.hanieum.llmproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 로그인과 로그아웃, 회원가입 기능을 구현한 서비스 클래스
 *
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final InMemoryUserDetailsManager inMemoryUserDetailsManager; (spring security떄문에 주석으로 해둠)


    @Override
    public User join(String username, String password) {
        return null;
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        Optional<User> optionalUser = userRepository.findById(requestDto.getUserId());

        // userId를 찾을 수 없을 경우
        if (optionalUser.isEmpty()) {
            return UserLoginResponseDto.builder()
                    .message("User not found")
                    .token(null)
                    .build();
        }

        User user = optionalUser.get();
        // password가 일치하지 않는 경우
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            return UserLoginResponseDto.builder()
                    .message("Invalid Password")
                    .token(null)
                    .build();
        }

        // 로그인 성공 시 Jwt 생성하여 ResponseDto token에 담기
        String token = generateToken(user.getUserId());
        return UserLoginResponseDto.builder()
                .message("Login Success")
                .token(token)
                .build();
    }

    private String generateToken(String userId) {
        // Fixme JwtUtil.generateToken 호출
        return "";
    }

    //회원가입
    @Override
    public Long signUp(UserSignupRequestDto requestDto) {

        //검증
        if (userRepository.findByUserId(requestDto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디 입니다.");
        }
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다.");
        }

        // 등록
        User user = userRepository.save(requestDto.toEntity());

        // todo passwordEncoder 적용
        return user.getUserNo();
    }
}


