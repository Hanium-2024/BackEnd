package com.hanieum.llmproject.dto.chat;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRequestDto {
	String model;
	@JsonProperty("max_tokens")
	Integer maxTokens;
	Double temperature;
	boolean stream;
	List<ChatMessage> messages;

}
