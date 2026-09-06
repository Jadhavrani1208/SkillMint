package com.skillmint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class ProfileDtos {
    @Builder
    public record ProfileResponse(
            Long id,
            String username,
            String fullName,
            String email,
            Integer coins,
            String location,
            String bio
    ) {}

    public record UpdateProfileRequest(
            @NotBlank String fullName,
            @Email String email,
            String location,
            String bio
    ) {}
}
