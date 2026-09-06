package com.skillmint.controller;

import com.skillmint.dto.ProfileDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ProfileDtos.ProfileResponse getProfile(Authentication authentication) {
        return profileService.getProfile(AuthUser.id(authentication));
    }

    @PutMapping
    public ProfileDtos.ProfileResponse updateProfile(Authentication authentication,
                                                     @Valid @RequestBody ProfileDtos.UpdateProfileRequest request) {
        return profileService.updateProfile(AuthUser.id(authentication), request);
    }
}
