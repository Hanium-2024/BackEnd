package com.hanieum.llmproject.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom {
	@Id
	@Column(name = "CHATROOM_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;
	private boolean saved;
	private String title;

	// 채팅저장관련
	public Chatroom(User user, Long chatroomId, String title) {
		this.user = user;
		this.id = chatroomId;
		this.title = title;
	}

	public static Map<Long, String> getSavedChatrooms(List<Chatroom> chatroomList) {
		return chatroomList.stream()
			.filter(Chatroom::isSaved)
			.collect(Collectors.toMap(Chatroom::getChatroomId, Chatroom::getTitle));
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	public Long getChatroomId() {
		return id;
	}

	private String getTitle() {
		return title;
	}
}
