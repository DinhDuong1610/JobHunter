package com.dinhduong.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.Role;
import com.dinhduong.jobhunter.domain.response.ResultPaginationDTO;
import com.dinhduong.jobhunter.service.RoleService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) throws IdInvalidException {
        if (role.getName() != null && this.roleService.existsByName(role.getName())) {
            throw new IdInvalidException("Role name = " + role.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role role) throws IdInvalidException {
        if (this.roleService.fetchById(role.getId()) == null) {
            throw new IdInvalidException("Role id = " + role.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.roleService.update(role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> getAll(Pageable pageable) {
        return ResponseEntity.ok(this.roleService.fetchAll(pageable));
    }
}
