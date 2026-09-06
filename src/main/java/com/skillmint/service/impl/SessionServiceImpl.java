package com.skillmint.service.impl;

import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.entity.WalletTransaction;
import com.skillmint.domain.enums.*;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.repository.UserRepository;
import com.skillmint.repository.WalletTransactionRepository;
import com.skillmint.service.NotificationService;
import com.skillmint.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void markComplete(Long actingUserId, Long requestId) {
        SkillRequest req = requireActiveSession(actingUserId, requestId);
        if (actingUserId.equals(req.getSender().getId())) {
            req.setSenderCompleted(true);
        } else {
            req.setReceiverCompleted(true);
        }
        skillRequestRepository.save(req);
        Long otherId = otherUserId(req, actingUserId);
        notificationService.create(otherId, NotificationType.SESSION_COMPLETION_REQUESTED,
                displayName(actingUserId, req) + " marked the session as completed.");
    }

    @Override
    @Transactional
    public void confirmCompletion(Long actingUserId, Long requestId) {
        SkillRequest req = requireActiveSession(actingUserId, requestId);
        if (actingUserId.equals(req.getSender().getId())) {
            req.setSenderCompleted(true);
        } else {
            req.setReceiverCompleted(true);
        }

        if (Boolean.TRUE.equals(req.getSenderCompleted()) && Boolean.TRUE.equals(req.getReceiverCompleted())) {
            req.setSessionStatus(SessionStatus.COMPLETED);
            req.setCompletedAt(Instant.now());

            if (req.getRequestType() == RequestType.COIN_SESSION) {
                Integer amount = req.getCoinsPerSession();
                if (amount == null || amount <= 0) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "coinsPerSession missing for COIN_SESSION");
                }
                // Sender is learner/payer, receiver is teacher/earner
                User payer = req.getSender();
                User teacher = req.getReceiver();
                int payerCoins = payer.getCoins() == null ? 0 : payer.getCoins();
                if (payerCoins < amount) throw new ApiException(HttpStatus.BAD_REQUEST, "Sender has insufficient coins");
                payer.setCoins(payerCoins - amount);
                userRepository.save(payer);
                walletTransactionRepository.save(WalletTransaction.builder()
                        .user(payer)
                        .amount(amount)
                        .type(TransactionType.SPEND)
                        .description("Session completed: " + req.getSkill().getName())
                        .build());

                int teacherCoins = teacher.getCoins() == null ? 0 : teacher.getCoins();
                teacher.setCoins(teacherCoins + amount);
                userRepository.save(teacher);
                walletTransactionRepository.save(WalletTransaction.builder()
                        .user(teacher)
                        .amount(amount)
                        .type(TransactionType.EARN)
                        .description("Session completed: " + req.getSkill().getName())
                        .build());
            }

            notificationService.create(req.getSender().getId(), NotificationType.SESSION_COMPLETED,
                    "Session successfully completed.");
            notificationService.create(req.getReceiver().getId(), NotificationType.SESSION_COMPLETED,
                    "Session successfully completed.");
        } else {
            Long otherId = otherUserId(req, actingUserId);
            notificationService.create(otherId, NotificationType.SESSION_COMPLETION_REQUESTED,
                    displayName(actingUserId, req) + " confirmed completion. Please confirm to finish.");
        }

        skillRequestRepository.save(req);
    }

    @Override
    @Transactional
    public void terminate(Long actingUserId, Long requestId) {
        SkillRequest req = requireActiveSession(actingUserId, requestId);
        req.setSessionStatus(SessionStatus.TERMINATED);
        req.setTerminatedAt(Instant.now());
        skillRequestRepository.save(req);
        Long otherId = otherUserId(req, actingUserId);
        notificationService.create(otherId, NotificationType.SESSION_TERMINATED,
                displayName(actingUserId, req) + " terminated the session.");
    }

    private SkillRequest requireActiveSession(Long actingUserId, Long requestId) {
        SkillRequest req = skillRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Request not found"));
        boolean isParticipant = req.getSender().getId().equals(actingUserId) || req.getReceiver().getId().equals(actingUserId);
        if (!isParticipant) throw new ApiException(HttpStatus.FORBIDDEN, "Forbidden");
        if (req.getStatus() != RequestStatus.ACCEPTED) throw new ApiException(HttpStatus.BAD_REQUEST, "Request is not accepted");
        if (req.getSessionStatus() != SessionStatus.ACTIVE) throw new ApiException(HttpStatus.BAD_REQUEST, "Session is not active");
        return req;
    }

    private Long otherUserId(SkillRequest req, Long actingUserId) {
        return req.getSender().getId().equals(actingUserId) ? req.getReceiver().getId() : req.getSender().getId();
    }

    private String displayName(Long actingUserId, SkillRequest req) {
        if (req.getSender().getId().equals(actingUserId)) return req.getSender().getFullName();
        return req.getReceiver().getFullName();
    }
}

