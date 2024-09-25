package com.hanieum.llmproject.repository;

import com.hanieum.llmproject.model.Chatroom;
import com.hanieum.llmproject.model.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {

    List<Retrospect> findAllByChatroom(Chatroom chatroom);
}
