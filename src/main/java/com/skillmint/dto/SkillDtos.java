package com.skillmint.dto;

import com.skillmint.domain.enums.SkillType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class SkillDtos {
    public record CreateSkillRequest(
            @NotBlank String name,
            @NotBlank String level,
            @NotNull SkillType type
    ) {}

    @Builder
    public record SkillResponse(Long id, String name, String level, SkillType type, Long userId) {}
}
