package com.hanieum.llmproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.hanieum.llmproject.model.RefreshToken;
import com.hanieum.llmproject.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByUser(User user);

	@Transactional
	void deleteByUser(User user);
}
