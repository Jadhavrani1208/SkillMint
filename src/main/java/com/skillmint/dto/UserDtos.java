package com.skillmint.dto;

import com.skillmint.domain.enums.SkillType;
import lombok.Builder;

import java.util.List;

public class UserDtos {
    @Builder
    public record SkillSummary(Long id, String name, String level, SkillType type) {}

    @Builder
    public record UserDiscoveryResponse(
            Long id,
            String fullName,
            String bio,
            String location,
            List<SkillSummary> teachSkills,
            List<SkillSummary> learnSkills
    ) {}
}
