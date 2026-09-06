package com.skillmint.service.impl;

import com.skillmint.domain.entity.Notification;
import com.skillmint.domain.enums.NotificationType;
import com.skillmint.dto.NotificationDtos;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.NotificationRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public void create(Long userId, NotificationType type, String message) {
        notificationRepository.save(Notification.builder()
                .user(currentUserService.requireUser(userId))
                .type(type)
                .message(message)
                .read(false)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDtos.NotificationResponse> list(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(n -> NotificationDtos.NotificationResponse.builder()
                        .id(n.getId())
                        .type(n.getType())
                        .message(n.getMessage())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!n.getUser().getId().equals(userId)) throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        n.setRead(true);
        notificationRepository.save(n);
    }
}

