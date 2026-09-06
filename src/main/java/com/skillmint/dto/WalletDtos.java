package com.skillmint.dto;

import com.skillmint.domain.enums.TransactionType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

public class WalletDtos {
    @Builder
    public record WalletTransactionResponse(
            Long id,
            Integer amount,
            TransactionType type,
            String description,
            Instant createdAt
    ) {}

    @Builder
    public record WalletSummaryResponse(Integer balance, Integer totalEarned, Integer totalSpent, List<WalletTransactionResponse> transactions) {}
}
