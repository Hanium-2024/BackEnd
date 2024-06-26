package com.hanieum.llmproject.repository;

import com.hanieum.llmproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
    boolean existsByEmail(String email);
}
