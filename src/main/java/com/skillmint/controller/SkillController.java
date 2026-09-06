package com.skillmint.controller;

import com.skillmint.domain.enums.SkillType;
import com.skillmint.dto.SkillDtos;
import com.skillmint.security.AuthUser;
import com.skillmint.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public SkillDtos.SkillResponse addSkill(Authentication authentication,
                                            @Valid @RequestBody SkillDtos.CreateSkillRequest request) {
        return skillService.addSkill(AuthUser.id(authentication), request);
    }

    @DeleteMapping("/{skillId}")
    public void deleteSkill(Authentication authentication, @PathVariable Long skillId) {
        skillService.removeSkill(AuthUser.id(authentication), skillId);
    }

    @GetMapping
    public List<SkillDtos.SkillResponse> list(Authentication authentication,
                                              @RequestParam(required = false) SkillType type) {
        return skillService.getSkills(AuthUser.id(authentication), type);
    }
}
