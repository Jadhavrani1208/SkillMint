package com.skillmint.service;

public interface SessionService {
    void markComplete(Long actingUserId, Long requestId);
    void confirmCompletion(Long actingUserId, Long requestId);
    void terminate(Long actingUserId, Long requestId);
}

