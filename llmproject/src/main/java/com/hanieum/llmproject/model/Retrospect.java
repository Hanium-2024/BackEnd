package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Retrospect {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chatroom의 PK를 공유

    @ManyToOne
    @JoinColumn(name = "CHATROOM_ID")
    private Chatroom chatroom;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] response;

    private LocalDateTime createdAt;

    public Retrospect(Chatroom chatroom) {
        this.chatroom = chatroom;
        response = "저장된 응답 없음".getBytes();
        this.createdAt = LocalDateTime.now();
    }

    public void setResponse(String response) {
        this.response = response.getBytes();
    }
}
