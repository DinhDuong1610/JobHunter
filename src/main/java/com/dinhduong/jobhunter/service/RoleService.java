package com.dinhduong.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Permission;
import com.dinhduong.jobhunter.domain.Role;
import com.dinhduong.jobhunter.repository.PermissionRepository;
import com.dinhduong.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    public Role create(Role role) {
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(role);
    }

    public Role fetchById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        return null;
    }

    public Role update(Role role) {
        if (role.getPermissions() != null) {
            List<Long> reqPermissions = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            role.setPermissions(dbPermissions);
        }

        Role roleDB = this.fetchById(role.getId());
        roleDB.setActive(role.isActive());
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setPermissions(role.getPermissions());

        return this.roleRepository.save(roleDB);
    }
}
