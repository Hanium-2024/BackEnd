package com.hanieum.llmproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanieum.llmproject.model.Category;
import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.User;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	List<Chatroom> findAllByUserAndCategory(User user, Category category);

	Optional<Chatroom> findByUserAndId(User user, Long id);

	Optional<Chatroom> findByUserAndCategoryAndId(User user, Category category, Long id);

	boolean existsById(Long id);
}
