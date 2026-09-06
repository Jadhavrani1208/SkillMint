package com.skillmint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

public class MessageDtos {
    public record SendMessageRequest(@NotNull Long receiverId, @NotBlank String content) {}

    @Builder
    public record MessageResponse(
            Long id,
            Long senderId,
            Long receiverId,
            String content,
            Instant timestamp
    ) {}

    @Builder
    public record ContactResponse(Long userId, String fullName) {}
}
