package com.dinhduong.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.Permission;
import com.dinhduong.jobhunter.service.PermissionService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission name = " + permission.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@Valid @RequestBody Permission permission) throws IdInvalidException {
        if (this.permissionService.fetchById(permission.getId()) == null) {
            throw new IdInvalidException("Permission name = " + permission.getName() + " khong ton tai");
        }

        if (this.permissionService.isPermissionExist(permission)) {
            throw new IdInvalidException("Permission name = " + permission.getName() + " đã tồn tại");
        }

        return ResponseEntity.ok().body(this.permissionService.update(permission));
    }
}
