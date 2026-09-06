package com.skillmint.controller;

import com.skillmint.dto.DashboardDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardDtos.DashboardStatsResponse stats(Authentication authentication) {
        return dashboardService.stats(AuthUser.id(authentication));
    }
}
