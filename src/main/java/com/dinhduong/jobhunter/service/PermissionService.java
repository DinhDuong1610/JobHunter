package com.dinhduong.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Permission;
import com.dinhduong.jobhunter.domain.response.ResultPaginationDTO;
import com.dinhduong.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission create(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }
        return null;
    }

    public Permission update(Permission permission) {
        Permission permissionDB = this.fetchById(permission.getId());
        permissionDB.setName(permission.getName());
        permissionDB.setModule(permission.getModule());
        permissionDB.setApiPath(permission.getApiPath());
        permissionDB.setMethod(permission.getMethod());

        return this.permissionRepository.save(permissionDB);
    }

    public void delete(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        Permission currentPermission = permissionOptional.get();
        currentPermission.getRoles().forEach(role -> role.getPermissions().remove(currentPermission));
        this.permissionRepository.delete(currentPermission);
    }

    public ResultPaginationDTO fetchAll(Pageable pageable) {
        Page<Permission> pagePermission = this.permissionRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());
        return rs;
    }
}
