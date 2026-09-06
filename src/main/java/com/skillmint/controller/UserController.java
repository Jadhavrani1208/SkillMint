package com.skillmint.controller;

import com.skillmint.dto.UserDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDtos.UserDiscoveryResponse> list(Authentication authentication) {
        return userService.discoverUsers(AuthUser.id(authentication));
    }
}
