package com.hanieum.llmproject.repository;

import com.hanieum.llmproject.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByLoginId(String loginId);
    @Transactional
    void deleteByLoginId(String loginId);
}
