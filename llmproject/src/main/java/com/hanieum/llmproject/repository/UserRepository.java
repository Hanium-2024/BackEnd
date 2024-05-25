package com.hanieum.llmproject.repository;

import com.hanieum.llmproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
