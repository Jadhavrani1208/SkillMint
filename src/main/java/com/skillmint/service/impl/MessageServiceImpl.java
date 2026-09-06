package com.skillmint.service.impl;

import com.skillmint.domain.entity.Message;
import com.skillmint.domain.entity.SkillRequest;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.SessionStatus;
import com.skillmint.dto.MessageDtos;
import com.skillmint.repository.MessageRepository;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.repository.UserRepository;
import com.skillmint.exception.ApiException;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public MessageDtos.MessageResponse send(Long senderId, MessageDtos.SendMessageRequest request) {
        requireAcceptedPartnership(senderId, request.receiverId());
        Message saved = messageRepository.save(Message.builder()
                .sender(currentUserService.requireUser(senderId))
                .receiver(currentUserService.requireUser(request.receiverId()))
                .content(request.content())
                .build());
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDtos.MessageResponse> conversation(Long userId, Long withUserId) {
        requireAcceptedPartnership(userId, withUserId);
        return messageRepository.findConversation(userId, withUserId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDtos.ContactResponse> contacts(Long userId) {
        Map<Long, String> partners = new LinkedHashMap<>();

        for (SkillRequest req : skillRequestRepository.findByReceiverId(userId)) {
            if (req.getStatus() == RequestStatus.ACCEPTED) {
                User sender = req.getSender();
                partners.put(sender.getId(), sender.getFullName());
            }
        }
        for (SkillRequest req : skillRequestRepository.findBySenderId(userId)) {
            if (req.getStatus() == RequestStatus.ACCEPTED) {
                User receiver = req.getReceiver();
                partners.put(receiver.getId(), receiver.getFullName());
            }
        }

        for (Long partnerId : messageRepository.findDistinctReceiverIdsBySenderId(userId)) {
            userRepository.findById(partnerId).ifPresent(u -> partners.put(u.getId(), u.getFullName()));
        }
        for (Long partnerId : messageRepository.findDistinctSenderIdsByReceiverId(userId)) {
            userRepository.findById(partnerId).ifPresent(u -> partners.put(u.getId(), u.getFullName()));
        }

        return partners.entrySet().stream()
                .map(e -> MessageDtos.ContactResponse.builder()
                        .userId(e.getKey())
                        .fullName(e.getValue())
                        .build())
                .toList();
    }

    private void requireAcceptedPartnership(Long userId, Long otherUserId) {
        if (userId.equals(otherUserId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot message yourself");
        }
        boolean allowed = skillRequestRepository.findByReceiverId(userId).stream()
                .anyMatch(r -> r.getStatus() == RequestStatus.ACCEPTED
                        && r.getSessionStatus() == SessionStatus.ACTIVE
                        && r.getSender().getId().equals(otherUserId))
                || skillRequestRepository.findBySenderId(userId).stream()
                .anyMatch(r -> r.getStatus() == RequestStatus.ACCEPTED
                        && r.getSessionStatus() == SessionStatus.ACTIVE
                        && r.getReceiver().getId().equals(otherUserId));
        if (!allowed) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Messaging is only available for active sessions");
        }
    }

    private MessageDtos.MessageResponse toDto(Message message) {
        return MessageDtos.MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .receiverId(message.getReceiver().getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
