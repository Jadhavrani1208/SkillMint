package com.skillmint.service.impl;

import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.entity.User;
import com.skillmint.domain.enums.SkillType;
import com.skillmint.dto.UserDtos;
import com.skillmint.repository.SkillRepository;
import com.skillmint.repository.UserRepository;
import com.skillmint.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Override
    public List<UserDtos.UserDiscoveryResponse> discoverUsers(Long currentUserId) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(this::toDiscoveryDto)
                .toList();
    }

    private UserDtos.UserDiscoveryResponse toDiscoveryDto(User user) {
        List<Skill> skills = skillRepository.findByUserId(user.getId());
        List<UserDtos.SkillSummary> teach = skills.stream()
                .filter(s -> s.getType() == SkillType.TEACH)
                .map(this::toSkillSummary)
                .toList();
        List<UserDtos.SkillSummary> learn = skills.stream()
                .filter(s -> s.getType() == SkillType.LEARN)
                .map(this::toSkillSummary)
                .toList();
        return UserDtos.UserDiscoveryResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .location(user.getLocation())
                .teachSkills(teach)
                .learnSkills(learn)
                .build();
    }

    private UserDtos.SkillSummary toSkillSummary(Skill skill) {
        return UserDtos.SkillSummary.builder()
                .id(skill.getId())
                .name(skill.getName())
                .level(skill.getLevel())
                .type(skill.getType())
                .build();
    }
}
