package com.dinhduong.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.User;
import com.dinhduong.jobhunter.domain.dto.ResCreateUserDTO;
import com.dinhduong.jobhunter.domain.dto.ResUpdateUserDTO;
import com.dinhduong.jobhunter.domain.dto.ResUserDTO;
import com.dinhduong.jobhunter.domain.dto.ResultPaginationDTO;
import com.dinhduong.jobhunter.service.UserService;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new User")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User newUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(newUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + newUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
        }

        String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User có id = " + id + " không tồn tại");
        }

        User user = this.userService.fetchUserById(id);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(user));

    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> getAllUsers(
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUsers(pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User userUpdate = this.userService.handleUpdateUser(user);
        if (userUpdate == null) {
            throw new IdInvalidException("User có id = " + user.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));
    }
}
