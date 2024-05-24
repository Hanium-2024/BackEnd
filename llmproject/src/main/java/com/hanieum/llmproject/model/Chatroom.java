package com.hanieum.llmproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Chatroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ChatroomId;
    private Long categoryId;
    private Long userNo;
}
