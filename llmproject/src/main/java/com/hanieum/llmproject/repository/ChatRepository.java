package com.hanieum.llmproject.repository;

import java.util.List;

import com.hanieum.llmproject.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hanieum.llmproject.model.Chat;
import com.hanieum.llmproject.model.Chatroom;

public interface ChatRepository extends JpaRepository<Chat, Long> {
	List<Chat> findAllByChatroomAndCategory(Chatroom chatroom, Category category);
}
