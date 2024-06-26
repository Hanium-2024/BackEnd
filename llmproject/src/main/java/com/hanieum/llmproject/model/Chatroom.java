package com.hanieum.llmproject.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
public class Chatroom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHATROOM_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    private boolean saved;
    private String title;

    public boolean isSaved() {
        return saved;
    }

    public static Map<Long, String> getChatrooms(List<Chatroom> chatroomList) {
        return chatroomList.stream()
                .filter(Chatroom::isSaved)
                .collect(Collectors.toMap(Chatroom::getChatroomId, Chatroom::getTitle));
    }

    private Long getChatroomId() {
        return id;
    }

    private String getTitle() {
        return title;
    }
}
