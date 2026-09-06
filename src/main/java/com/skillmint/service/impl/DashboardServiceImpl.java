package com.skillmint.service.impl;

import com.skillmint.domain.enums.RequestStatus;
import com.skillmint.domain.enums.SkillType;
import com.skillmint.dto.DashboardDtos;
import com.skillmint.repository.MessageRepository;
import com.skillmint.repository.SkillRepository;
import com.skillmint.repository.SkillRequestRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final CurrentUserService currentUserService;
    private final SkillRequestRepository requestRepository;
    private final MessageRepository messageRepository;
    private final SkillRepository skillRepository;

    @Override
    public DashboardDtos.DashboardStatsResponse stats(Long userId) {
        var user = currentUserService.requireUser(userId);
        Integer safeCoins = user.getCoins() == null ? 0 : user.getCoins();
        return DashboardDtos.DashboardStatsResponse.builder()
                .coins(safeCoins)
                .activeRequests(requestRepository.countByReceiverIdAndStatus(userId, RequestStatus.PENDING))
                .unreadMessages(messageRepository.countByReceiverId(userId))
                .skillsTeach((long) skillRepository.findByUserIdAndType(userId, SkillType.TEACH).size())
                .skillsLearn((long) skillRepository.findByUserIdAndType(userId, SkillType.LEARN).size())
                .build();
    }
}
