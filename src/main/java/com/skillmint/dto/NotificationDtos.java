package com.skillmint.dto;

import com.skillmint.domain.enums.NotificationType;
import lombok.Builder;

import java.time.Instant;

public class NotificationDtos {
    @Builder
    public record NotificationResponse(Long id, NotificationType type, String message, boolean read, Instant createdAt) {}
}

