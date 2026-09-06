package com.skillmint.service;

import com.skillmint.dto.MessageDtos;

import java.util.List;

public interface MessageService {
    MessageDtos.MessageResponse send(Long senderId, MessageDtos.SendMessageRequest request);
    List<MessageDtos.MessageResponse> conversation(Long userId, Long withUserId);
    List<MessageDtos.ContactResponse> contacts(Long userId);
}
