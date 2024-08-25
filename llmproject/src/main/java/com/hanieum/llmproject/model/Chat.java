package com.hanieum.llmproject.model;

import java.time.LocalDateTime;
import java.util.Arrays;

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

@Entity
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
	private ContentType contentType;
	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] content;
	private LocalDateTime outputTime;

	// 메소드
	public Chat(Chatroom chatroomId, boolean isUserMessage, boolean isImage, String message) {
		this.chatroom = chatroomId;

		// TODO subString으로 IMAGE_output식별로 분리해서 저장
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

	public boolean isUserMessage() {
		return this.contentType == ContentType.Prompt;
	}

	// Enum 타입 정의
	private enum ContentType {
		Prompt,
		Text_output,
		Image_output;
	}

}
