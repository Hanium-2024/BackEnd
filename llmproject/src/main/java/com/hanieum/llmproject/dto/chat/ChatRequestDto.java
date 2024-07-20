package com.hanieum.llmproject.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

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
