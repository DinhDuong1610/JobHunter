package com.dinhduong.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dinhduong.jobhunter.domain.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    boolean existsByName(String name);

    List<Skill> findByIdIn(List<Long> skills);

}