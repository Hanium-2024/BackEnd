package com.hanieum.llmproject.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHATROOM_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;
	private String title;

	@OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Chat> chats;

	// 채팅저장관련
	public Chatroom(User user, String title) {
		this.user = user;
		this.title = title;
	}

	public static Map<Long, String> getSavedChatrooms(List<Chatroom> chatroomList) {
		return chatroomList.stream()
			.collect(Collectors.toMap(Chatroom::getChatroomId, Chatroom::getTitle));
	}

	public Long getChatroomId() {
		return id;
	}

	private String getTitle() {
		return title;
	}
}
