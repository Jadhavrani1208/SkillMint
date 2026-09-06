package com.skillmint.service;

import com.skillmint.dto.ProfileDtos;

public interface ProfileService {
    ProfileDtos.ProfileResponse getProfile(Long userId);
    ProfileDtos.ProfileResponse updateProfile(Long userId, ProfileDtos.UpdateProfileRequest request);
}
