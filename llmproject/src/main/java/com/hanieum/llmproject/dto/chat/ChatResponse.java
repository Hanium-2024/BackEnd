package com.hanieum.llmproject.dto.chat;

import java.util.List;

public class ChatResponse {
    public record RetrospectQuestionHistory(
            String topic,
            String keepContent,
            String problemContent,
            String tryContent
    ) {
    }

    public record RetrospectHistory(
            List<RetrospectQuestionHistory> questionHistories,
            String response
    ) {
    }

    public record CommonHistory(
            String chatId,
            boolean selected,
            String message
    ) {
    }
}
