package com.dinhduong.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dinhduong.jobhunter.domain.User;
import com.dinhduong.jobhunter.domain.request.ReqLoginDTO;
import com.dinhduong.jobhunter.domain.response.ResCreateUserDTO;
import com.dinhduong.jobhunter.domain.response.ResLoginDTO;
import com.dinhduong.jobhunter.service.UserService;
import com.dinhduong.jobhunter.util.SecurityUtil;
import com.dinhduong.jobhunter.util.annotation.ApiMessage;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;
        private final PasswordEncoder passwordEncoder;

        @Value("${dinhduong.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService, PasswordEncoder passwordEncoder) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
                this.passwordEncoder = passwordEncoder;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();

                User currentUser = this.userService.handleGetUsername(loginDTO.getUsername());

                if (currentUser != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUser.getId(),
                                        currentUser.getEmail(),
                                        currentUser.getName(),
                                        currentUser.getRole());

                        res.setUser(userLogin);
                }

                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
                res.setAccessToken(access_token);

                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUser = this.userService.handleGetUsername(email);

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                currentUser.getId(),
                                currentUser.getEmail(),
                                currentUser.getName(),
                                currentUser.getRole());

                return ResponseEntity.ok().body(userLogin);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("Get User by refresh token")
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "error") String refresh_token)
                        throws IdInvalidException {

                if (refresh_token.equals("error")) {
                        throw new IdInvalidException("Không có token ở cookie gửi lên");
                }
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();

                User currentUserDB = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);

                if (currentUserDB == null) {
                        throw new IdInvalidException("Refresh token không hợp lệ");
                }

                ResLoginDTO res = new ResLoginDTO();

                User currentUser = this.userService.handleGetUsername(email);

                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getEmail(),
                                currentUser.getName(), currentUser.getRole());

                res.setUser(userLogin);

                String access_token = this.securityUtil.createAccessToken(email, res);
                res.setAccessToken(access_token);

                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
                this.userService.updateUserToken(new_refresh_token, email);

                ResponseCookie resCookies = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                                .body(res);
        }

        @GetMapping("/auth/logout")
        @ApiMessage("Logout User")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                if (email.equals("")) {
                        throw new IdInvalidException("Access Token Không hợp lệ");
                }

                this.userService.updateUserToken(null, email);

                ResponseCookie deleteCookies = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteCookies.toString())
                                .body(null);
        }

        @PostMapping("/auth/register")
        @ApiMessage("Register a new user")
        public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User newUser)
                        throws IdInvalidException {
                boolean isEmailExist = this.userService.isEmailExist(newUser.getEmail());
                if (isEmailExist) {
                        throw new IdInvalidException(
                                        "Email " + newUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác");
                }

                String hashPassword = this.passwordEncoder.encode(newUser.getPassword());
                newUser.setPassword(hashPassword);
                User user = this.userService.handleCreateUser(newUser);
                return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
        }
}
