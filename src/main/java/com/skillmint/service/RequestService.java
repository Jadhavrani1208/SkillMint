package com.skillmint.service;

import com.skillmint.dto.RequestDtos;

import java.util.List;

public interface RequestService {
    RequestDtos.SkillRequestResponse createRequest(Long senderId, RequestDtos.CreateRequestRequest request);
    RequestDtos.SkillRequestResponse updateStatus(Long requestId, Long actingUserId, RequestDtos.UpdateRequestStatusRequest request);
    List<RequestDtos.SkillRequestResponse> received(Long userId);
    List<RequestDtos.SkillRequestResponse> sent(Long userId);
}
