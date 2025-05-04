package com.dinhduong.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Skill;
import com.dinhduong.jobhunter.domain.response.ResultPaginationDTO;
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

    public ResultPaginationDTO fetchAllSkills(Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageSkill.getContent());

        return rs;
    }

}
