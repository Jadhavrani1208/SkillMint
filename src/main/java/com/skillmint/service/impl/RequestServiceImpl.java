package com.skillmint.service.impl;

import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.RequestType;
import com.skillmint.domain.enums.SessionStatus;
import com.skillmint.dto.RequestDtos;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.SkillRepository;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.NotificationService;
import com.skillmint.domain.enums.NotificationType;
import com.skillmint.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final SkillRequestRepository requestRepository;
    private final SkillRepository skillRepository;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;

    @Override
    public RequestDtos.SkillRequestResponse createRequest(Long senderId, RequestDtos.CreateRequestRequest request) {
        if (request.requestType() == null) throw new ApiException(HttpStatus.BAD_REQUEST, "requestType is required");
        if (request.requestType() == RequestType.COIN_SESSION) {
            if (request.coinsPerSession() == null || request.coinsPerSession() <= 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "coinsPerSession must be greater than 0");
            }
        }
        if (senderId.equals(request.receiverId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot create request for yourself");
        }
        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Skill not found"));
        if (!skill.getUser().getId().equals(request.receiverId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Skill does not belong to selected receiver");
        }
        SkillRequest saved = requestRepository.save(SkillRequest.builder()
                .sender(currentUserService.requireUser(senderId))
                .receiver(currentUserService.requireUser(request.receiverId()))
                .skill(skill)
                .requestType(request.requestType())
                .coinsPerSession(request.requestType() == RequestType.COIN_SESSION ? request.coinsPerSession() : null)
                .status(RequestStatus.PENDING)
                .sessionStatus(null)
                .build());
        notificationService.create(request.receiverId(), NotificationType.REQUEST_RECEIVED,
                saved.getSender().getFullName() + " sent you a request.");
        return toDto(saved);
    }

    @Override
    @Transactional
    public RequestDtos.SkillRequestResponse updateStatus(Long requestId, Long actingUserId, RequestDtos.UpdateRequestStatusRequest request) {
        SkillRequest entity = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Request not found"));
        if (!entity.getReceiver().getId().equals(actingUserId)) throw new ApiException(HttpStatus.FORBIDDEN, "Only receiver can update request");
        if (entity.getStatus() != RequestStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only pending requests can be updated");
        }
        RequestStatus nextStatus = request.status();
        if (nextStatus == RequestStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Request cannot be set back to pending");
        }

        entity.setStatus(nextStatus);
        if (nextStatus == RequestStatus.ACCEPTED) {
            entity.setSessionStatus(SessionStatus.ACTIVE);
            entity.setSenderCompleted(false);
            entity.setReceiverCompleted(false);
            notificationService.create(entity.getSender().getId(), NotificationType.REQUEST_ACCEPTED,
                    entity.getReceiver().getFullName() + " accepted your request.");
        }
        SkillRequest saved = requestRepository.save(entity);
        return toDto(saved);
    }

    @Override
    public List<RequestDtos.SkillRequestResponse> received(Long userId) {
        // Temporary debug logging: reconcile "badge count" vs "list count"
        long pendingCount = requestRepository.countByReceiverIdAndStatus(userId, RequestStatus.PENDING);
        List<SkillRequest> received = requestRepository.findReceived(userId);
        log.info("Requests.received userId={} pendingCount={} receivedCount={}", userId, pendingCount, received.size());
        return received.stream().map(this::toDto).toList();
    }

    @Override
    public List<RequestDtos.SkillRequestResponse> sent(Long userId) {
        List<SkillRequest> sent = requestRepository.findSent(userId);
        log.info("Requests.sent userId={} sentCount={}", userId, sent.size());
        return sent.stream().map(this::toDto).toList();
    }

    private RequestDtos.SkillRequestResponse toDto(SkillRequest req) {
        return RequestDtos.SkillRequestResponse.builder()
                .id(req.getId())
                .senderId(req.getSender().getId())
                .senderName(req.getSender().getFullName())
                .receiverId(req.getReceiver().getId())
                .receiverName(req.getReceiver().getFullName())
                .skillId(req.getSkill().getId())
                .skillName(req.getSkill().getName())
                .status(req.getStatus())
                .requestType(req.getRequestType())
                .sessionStatus(req.getSessionStatus())
                .coinsPerSession(req.getCoinsPerSession())
                .senderCompleted(Boolean.TRUE.equals(req.getSenderCompleted()))
                .receiverCompleted(Boolean.TRUE.equals(req.getReceiverCompleted()))
                .completedAt(req.getCompletedAt())
                .terminatedAt(req.getTerminatedAt())
                .createdAt(req.getCreatedAt())
                .build();
    }
}
