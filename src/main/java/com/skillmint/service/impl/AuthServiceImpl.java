package com.skillmint.service.impl;

import com.skillmint.domain.entity.User;
import com.skillmint.dto.AuthDtos;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.UserRepository;
import com.skillmint.security.JwtService;
import com.skillmint.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        if (userRepository.existsByUsername(request.username())) throw new ApiException(HttpStatus.CONFLICT, "Username already in use");
        User user = userRepository.save(User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .coins(100)
                .bio("Welcome to SkillMint!")
                .build());
        return auth(user);
    }

    @Override
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return auth(user);
    }

    private AuthDtos.AuthResponse auth(User user) {
        return AuthDtos.AuthResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .coins(user.getCoins())
                .token(jwtService.generateToken(user.getId(), user.getEmail()))
                .build();
    }
}
