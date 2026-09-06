package com.skillmint.controller;

import com.skillmint.dto.RequestDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.RequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDtos.SkillRequestResponse create(Authentication authentication,
                                                   @Valid @RequestBody RequestDtos.CreateRequestRequest request) {
        return requestService.createRequest(AuthUser.id(authentication), request);
    }

    @PatchMapping("/{requestId}/status")
    public RequestDtos.SkillRequestResponse updateStatus(@PathVariable Long requestId,
                                                         Authentication authentication,
                                                         @Valid @RequestBody RequestDtos.UpdateRequestStatusRequest request) {
        return requestService.updateStatus(requestId, AuthUser.id(authentication), request);
    }

    @GetMapping("/received")
    public List<RequestDtos.SkillRequestResponse> received(Authentication authentication) {
        return requestService.received(AuthUser.id(authentication));
    }

    @GetMapping("/sent")
    public List<RequestDtos.SkillRequestResponse> sent(Authentication authentication) {
        return requestService.sent(AuthUser.id(authentication));
    }
}
