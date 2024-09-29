package com.hanieum.llmproject.dto.chat;

public final class ChatRequest {
	public record Common(String question) {
	}

	public record Retrospect(
			Long chatId,
			String topic,
			String keepContent,
			String problemContent,
			String tryContent
	) {
	}
}
