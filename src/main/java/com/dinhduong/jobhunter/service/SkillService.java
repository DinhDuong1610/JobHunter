package com.dinhduong.jobhunter.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Skill;
import com.dinhduong.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

}
