package com.skillmint.controller;

import com.skillmint.dto.NotificationDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationDtos.NotificationResponse> list(Authentication authentication) {
        return notificationService.list(AuthUser.id(authentication));
    }

    @PatchMapping("/{id}/read")
    public void read(Authentication authentication, @PathVariable Long id) {
        notificationService.markRead(AuthUser.id(authentication), id);
    }
}

