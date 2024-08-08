package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private enum ContentType {
        Prompt,
        Text_output,
        Image_output;
    }

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime outputTime;

    // 메소드
    public Chat(Chatroom chatroomId, boolean isUserMessage, boolean isImage, String message) {
        this.chatroom = chatroomId;

        // TODO subString으로 IMAGE_output식별로 분리해서 저장
        this.contentType = determineContentType(isUserMessage, isImage);
        this.content = message;
        this.outputTime = LocalDateTime.now();
    }

    private ContentType determineContentType(boolean isUserMessage, boolean isImage) {
        if (isUserMessage) { return ContentType.Prompt; }

        if (isImage) { return ContentType.Image_output; }

        return ContentType.Text_output;
    }

    public LocalDateTime getOutputTime() { return outputTime; }

    public String getMessage() {
        return this.content;
    }

    public boolean isUserMessage() {
        return this.contentType == ContentType.Prompt;
    }

}
