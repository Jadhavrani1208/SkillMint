package com.skillmint.service.impl;

import com.skillmint.domain.entity.WalletTransaction;
import com.skillmint.domain.enums.TransactionType;
import com.skillmint.dto.WalletDtos;
import com.skillmint.repository.WalletTransactionRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletTransactionRepository repository;
    private final CurrentUserService currentUserService;

    @Override
    public WalletDtos.WalletSummaryResponse summary(Long userId) {
        var user = currentUserService.requireUser(userId);
        List<WalletTransaction> txns = repository.findByUserIdOrderByCreatedAtDesc(userId);
        int earned = txns.stream()
                .filter(t -> t.getType() == TransactionType.EARN || t.getType() == TransactionType.BONUS)
                .mapToInt(t -> t.getAmount() == null ? 0 : t.getAmount())
                .sum();
        int spent = txns.stream()
                .filter(t -> t.getType() == TransactionType.SPEND)
                .mapToInt(t -> t.getAmount() == null ? 0 : t.getAmount())
                .sum();
        int safeBalance = user.getCoins() == null ? 0 : user.getCoins();
        return WalletDtos.WalletSummaryResponse.builder()
                .balance(safeBalance)
                .totalEarned(earned)
                .totalSpent(spent)
                .transactions(txns.stream().map(t -> WalletDtos.WalletTransactionResponse.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .description(t.getDescription())
                        .createdAt(t.getCreatedAt())
                        .build()).toList())
                .build();
    }
}
