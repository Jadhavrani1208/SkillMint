package com.skillmint.service.impl;

import com.skillmint.domain.entity.User;
import com.skillmint.dto.ProfileDtos;
import com.skillmint.repository.UserRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    @Override
    public ProfileDtos.ProfileResponse getProfile(Long userId) {
        return toDto(currentUserService.requireUser(userId));
    }

    @Override
    public ProfileDtos.ProfileResponse updateProfile(Long userId, ProfileDtos.UpdateProfileRequest request) {
        User user = currentUserService.requireUser(userId);
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setLocation(request.location());
        user.setBio(request.bio());
        return toDto(userRepository.save(user));
    }

    private ProfileDtos.ProfileResponse toDto(User user) {
        return ProfileDtos.ProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .coins(user.getCoins())
                .location(user.getLocation())
                .bio(user.getBio())
                .build();
    }
}
