package com.skillmint.service;

import com.skillmint.dto.UserDtos;

import java.util.List;

public interface UserService {
    List<UserDtos.UserDiscoveryResponse> discoverUsers(Long currentUserId);
}
