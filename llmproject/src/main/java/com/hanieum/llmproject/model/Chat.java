package com.hanieum.llmproject.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CHATROOM_ID")
    private Chatroom chatroom;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    // Enum 타입 정의
    public enum ContentType {
        Prompt,
        Text_output,
        Image_output;
    }

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime outputTime;

    // 메소드
    public Chat() {}

    public Chat(Chatroom chatroomId, boolean isUserMessage, String message) {
        this.chatroom = chatroomId;

        if (isUserMessage) {
            this.contentType = ContentType.Prompt;
        } else {
            // TODO subString으로 IMAGE_output식별로 분리해서 저장
            this.contentType = ContentType.Text_output;
        }
        this.content = message;
        this.outputTime = LocalDateTime.now();

    }

    public boolean isUserMessage() {
        return this.contentType == ContentType.Prompt;
    }

    public String getMessage() {
        return this.content;
    }

}
