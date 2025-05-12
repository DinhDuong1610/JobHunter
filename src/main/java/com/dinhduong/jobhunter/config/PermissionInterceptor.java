package com.dinhduong.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.dinhduong.jobhunter.domain.Permission;
import com.dinhduong.jobhunter.domain.Role;
import com.dinhduong.jobhunter.domain.User;
import com.dinhduong.jobhunter.service.UserService;
import com.dinhduong.jobhunter.util.SecurityUtil;
import com.dinhduong.jobhunter.util.error.IdInvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : null;
        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean isAllow = permissions.stream()
                            .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));
                    if (isAllow == false) {
                        throw new IdInvalidException("Không có quyền truy cập");
                    }
                } else {
                    throw new IdInvalidException("Không có quyền truy cập");
                }
            }
        }

        return true;
    }
}
