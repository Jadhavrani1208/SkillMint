package com.skillmint.security;

import com.skillmint.exception.ApiException;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;

public final class AuthUser {
    private AuthUser() {}

    public static Long id(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long l) return l;
        if (principal instanceof Integer i) return i.longValue();
        try {
            return Long.parseLong(principal.toString());
        } catch (NumberFormatException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}
