package com.skillmint.service;

import com.skillmint.dto.WalletDtos;

public interface WalletService {
    WalletDtos.WalletSummaryResponse summary(Long userId);
}
