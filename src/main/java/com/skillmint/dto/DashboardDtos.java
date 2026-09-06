package com.skillmint.dto;

import lombok.Builder;

public class DashboardDtos {
    @Builder
    public record DashboardStatsResponse(
            Integer coins,
            Long activeRequests,
            Long unreadMessages,
            Long skillsTeach,
            Long skillsLearn
    ) {}
}
