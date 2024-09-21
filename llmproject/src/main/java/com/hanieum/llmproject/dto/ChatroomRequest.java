package com.hanieum.llmproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatroomRequest {

    public record Create(@NotBlank  String title) {
    }
}
