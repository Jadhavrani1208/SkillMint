package com.skillmint.controller;

import com.skillmint.security.AuthUser;
import com.skillmint.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping("/{id}/complete")
    public void complete(Authentication authentication, @PathVariable Long id) {
        sessionService.markComplete(AuthUser.id(authentication), id);
    }

    @PostMapping("/{id}/confirm-completion")
    public void confirm(Authentication authentication, @PathVariable Long id) {
        sessionService.confirmCompletion(AuthUser.id(authentication), id);
    }

    @PostMapping("/{id}/terminate")
    public void terminate(Authentication authentication, @PathVariable Long id) {
        sessionService.terminate(AuthUser.id(authentication), id);
    }
}

