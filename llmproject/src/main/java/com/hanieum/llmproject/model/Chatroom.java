package com.hanieum.llmproject.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom {
    @Id
    @Column(name = "CHATROOM_ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    private boolean saved;
    private String title;

    public boolean isSaved() {
        return saved;
    }

    public static Map<Long, String> getSavedChatrooms(List<Chatroom> chatroomList) {
        return chatroomList.stream()
                .filter(Chatroom::isSaved)
                .collect(Collectors.toMap(Chatroom::getChatroomId, Chatroom::getTitle));
    }

    public Long getChatroomId() {
        return id;
    }

    private String getTitle() {
        return title;
    }

    // 채팅저장관련
    public Chatroom(User user, Category category, String title) {
        this.user = user;
        this.category = category;
        this.title = title;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
}
