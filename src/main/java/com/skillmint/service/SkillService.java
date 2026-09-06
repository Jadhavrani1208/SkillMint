package com.skillmint.service;

import com.skillmint.domain.enums.SkillType;
import com.skillmint.dto.SkillDtos;

import java.util.List;

public interface SkillService {
    SkillDtos.SkillResponse addSkill(Long userId, SkillDtos.CreateSkillRequest request);
    void removeSkill(Long userId, Long skillId);
    List<SkillDtos.SkillResponse> getSkills(Long userId, SkillType type);
}
