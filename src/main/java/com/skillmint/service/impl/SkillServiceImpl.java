package com.skillmint.service.impl;

import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.enums.SkillType;
import com.skillmint.dto.SkillDtos;
import com.skillmint.exception.ApiException;
import com.skillmint.repository.SkillRepository;
import com.skillmint.service.CurrentUserService;
import com.skillmint.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillRepository skillRepository;
    private final CurrentUserService currentUserService;

    @Override
    public SkillDtos.SkillResponse addSkill(Long userId, SkillDtos.CreateSkillRequest request) {
        Skill saved = skillRepository.save(Skill.builder()
                .name(request.name())
                .level(request.level())
                .type(request.type())
                .user(currentUserService.requireUser(userId))
                .build());
        return toDto(saved);
    }

    @Override
    public void removeSkill(Long userId, Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Skill not found"));
        if (!skill.getUser().getId().equals(userId)) throw new ApiException(HttpStatus.FORBIDDEN, "Cannot remove another user's skill");
        skillRepository.delete(skill);
    }

    @Override
    public List<SkillDtos.SkillResponse> getSkills(Long userId, SkillType type) {
        List<Skill> skills = (type == null) ? skillRepository.findByUserId(userId) : skillRepository.findByUserIdAndType(userId, type);
        return skills.stream().map(this::toDto).toList();
    }

    private SkillDtos.SkillResponse toDto(Skill skill) {
        return SkillDtos.SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .level(skill.getLevel())
                .type(skill.getType())
                .userId(skill.getUser().getId())
                .build();
    }
}
