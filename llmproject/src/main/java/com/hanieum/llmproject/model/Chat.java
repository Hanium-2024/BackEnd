package com.hanieum.llmproject.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHAT_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "CHATROOM_ID")
	private Chatroom chatroom;

	@Enumerated(EnumType.STRING)
	private Category category;

	@Enumerated(EnumType.STRING)
	private ContentType contentType;
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] content;
	private LocalDateTime outputTime;

	private boolean retrospect;

	// 메소드
	public Chat(Chatroom chatroomId, Category category, boolean isUserMessage, boolean isImage, String message) {
		this.chatroom = chatroomId;
		this.category = category;
		this.contentType = determineContentType(isUserMessage, isImage);
		this.content = message.getBytes();
		this.outputTime = LocalDateTime.now();
	}

	private ContentType determineContentType(boolean isUserMessage, boolean isImage) {
		if (isUserMessage) {
			return ContentType.Prompt;
		}

		if (isImage) {
			return ContentType.Image_output;
		}

		return ContentType.Text_output;
	}

	public LocalDateTime getOutputTime() {
		return outputTime;
	}

	public String getMessage() {
		return new String(this.content);
	}

	public String getCategoryName() {
		return this.category.toString();
	}

	public boolean isUserMessage() {
		return this.contentType == ContentType.Prompt;
	}

	public boolean isRetrospect() { return retrospect; }

	public void setRetrospect(boolean retrospect) { this.retrospect = retrospect; }

	// Enum 타입 정의
	private enum ContentType {
		Prompt,
		Text_output,
		Image_output;
	}

}
