package com.dinhduong.jobhunter.service;

import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
