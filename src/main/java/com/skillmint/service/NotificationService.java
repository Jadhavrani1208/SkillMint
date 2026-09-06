package com.skillmint.service;

import com.skillmint.domain.enums.NotificationType;
import com.skillmint.dto.NotificationDtos;

import java.util.List;

public interface NotificationService {
    void create(Long userId, NotificationType type, String message);
    List<NotificationDtos.NotificationResponse> list(Long userId);
    void markRead(Long userId, Long notificationId);
}

