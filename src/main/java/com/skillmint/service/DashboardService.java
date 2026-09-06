package com.skillmint.service;

import com.skillmint.dto.DashboardDtos;

public interface DashboardService {
    DashboardDtos.DashboardStatsResponse stats(Long userId);
}
