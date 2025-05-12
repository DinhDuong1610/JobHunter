package com.dinhduong.jobhunter.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dinhduong.jobhunter.domain.Permission;
import com.dinhduong.jobhunter.domain.Role;
import com.dinhduong.jobhunter.domain.User;
import com.dinhduong.jobhunter.repository.PermissionRepository;
import com.dinhduong.jobhunter.repository.RoleRepository;
import com.dinhduong.jobhunter.repository.UserRepository;
import com.dinhduong.jobhunter.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository,
            UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();
            arr.add(new Permission("Create a company", "/api/v1/companies", "POST", "COMPANY"));
            arr.add(new Permission("Update a company", "/api/v1/companies/{id}", "PUT", "COMPANY"));
            arr.add(new Permission("Delete a company", "/api/v1/companies/{id}", "DELETE", "COMPANY"));
            arr.add(new Permission("Create a job", "/api/v1/jobs", "POST", "JOB"));
            arr.add(new Permission("Update a job", "/api/v1/jobs/{id}", "PUT", "JOB"));
            arr.add(new Permission("Delete a job", "/api/v1/jobs/{id}", "DELETE", "JOB"));
            arr.add(new Permission("Create a skill", "/api/v1/skills", "POST", "SKILL"));
            arr.add(new Permission("Update a skill", "/api/v1/skills/{id}", "PUT", "SKILL"));
            arr.add(new Permission("Delete a skill", "/api/v1/skills/{id}", "DELETE", "SKILL"));
            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLE"));
            arr.add(new Permission("Update a role", "/api/v1/roles/{id}", "PUT", "ROLE"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLE"));
            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSION"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions/{id}", "PUT", "PERMISSION"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSION"));
            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USER"));
            arr.add(new Permission("Update a user", "/api/v1/users/{id}", "PUT", "USER"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USER"));
            arr.add(new Permission("Login", "/api/v1/auth/login", "POST", "USER"));
            arr.add(new Permission("Refresh token", "/api/v1/auth/refresh", "POST", "USER"));
            arr.add(new Permission("Register user", "/api/v1/auth/register", "POST", "USER"));

            this.permissionRepository.saveAll(arr);
        }
        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setActive(true);
            adminRole.setDescription("Admin full permissions");
            adminRole.setPermissions(allPermissions);

            this.roleRepository.save(adminRole);
        }
        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));
            adminUser.setAge(20);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("ADMIN");
            adminUser.setAddress("Thua Thien Hue");

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null)
                adminUser.setRole(adminRole);

            this.userRepository.save(adminUser);

        }

        if (countPermissions == 0 || countRoles == 0 || countUsers == 0) {
            System.out.println(">>> END INIT DATABASE");
        }

    }

}
