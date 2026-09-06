package com.skillmint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String username,
            @Email String email,
            @NotBlank String password,
            @NotBlank String fullName
    ) {}

    public record LoginRequest(@Email String email, @NotBlank String password) {}

    @Builder
    public record AuthResponse(Long userId, String token, String fullName, Integer coins) {}
}
