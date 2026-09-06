package com.skillmint.repository;

import com.skillmint.domain.entity.Skill;
import com.skillmint.domain.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByUserId(Long userId);
    List<Skill> findByUserIdAndType(Long userId, SkillType type);
}
