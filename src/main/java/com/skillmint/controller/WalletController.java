package com.skillmint.controller;

import com.skillmint.dto.WalletDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/summary")
    public WalletDtos.WalletSummaryResponse summary(Authentication authentication) {
        return walletService.summary(AuthUser.id(authentication));
    }
}
