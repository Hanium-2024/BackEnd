package com.hanieum.llmproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	List<Chatroom> findAllByUser(User user);

	Optional<Chatroom> findByUserAndId(User user, Long id);

	boolean existsById(Long id);
}
