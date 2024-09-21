package com.hanieum.llmproject.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatroomResponse {

    public record Detail(Long id, String title) {
    }
}
