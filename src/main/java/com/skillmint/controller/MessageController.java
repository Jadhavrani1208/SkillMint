package com.skillmint.controller;

import com.skillmint.dto.MessageDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public MessageDtos.MessageResponse send(Authentication authentication,
                                            @Valid @RequestBody MessageDtos.SendMessageRequest request) {
        return messageService.send(AuthUser.id(authentication), request);
    }

    @GetMapping("/conversation/{withUserId}")
    public List<MessageDtos.MessageResponse> conversation(Authentication authentication,
                                                          @PathVariable Long withUserId) {
        return messageService.conversation(AuthUser.id(authentication), withUserId);
    }

    @GetMapping("/contacts")
    public List<MessageDtos.ContactResponse> contacts(Authentication authentication) {
        return messageService.contacts(AuthUser.id(authentication));
    }
}
