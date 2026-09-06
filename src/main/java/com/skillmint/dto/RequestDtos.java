package com.skillmint.dto;

import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.RequestType;
import com.skillmint.domain.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

public class RequestDtos {
    public record CreateRequestRequest(@NotNull Long receiverId,
                                       @NotNull Long skillId,
                                       @NotNull RequestType requestType,
                                       Integer coinsPerSession) {}
    public record UpdateRequestStatusRequest(@NotNull RequestStatus status) {}

    @Builder
    public record SkillRequestResponse(
            Long id,
            Long senderId,
            String senderName,
            Long receiverId,
            String receiverName,
            Long skillId,
            String skillName,
            RequestStatus status,
            RequestType requestType,
            SessionStatus sessionStatus,
            Integer coinsPerSession,
            boolean senderCompleted,
            boolean receiverCompleted,
            Instant completedAt,
            Instant terminatedAt,
            Instant createdAt
    ) {}
}
