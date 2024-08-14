package com.hanieum.llmproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanieum.llmproject.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByLoginId(String loginId);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);
}
