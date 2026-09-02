package com.hospital.resource.admin.service;

import com.hospital.resource.admin.dto.UserManagementRequest;
import com.hospital.resource.admin.dto.UserManagementResponse;
import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.UserRepository;
import com.hospital.resource.auth.service.PasswordService;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Transactional
    public UserManagementResponse createUser(UserManagementRequest request, UUID adminUserId) {
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .fullName(request.fullName())
                .role(request.role())
                .passwordHash(passwordService.encodePassword(request.password()))
                .status("ACTIVE")
                .createdBy(adminUserId)
                .updatedBy(adminUserId)
                .build();

        user = userRepository.save(user);
        log.info("User created by admin: userId={}, username={}", user.getId(), user.getUsername());
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserManagementResponse> getAllUsers() {
        return userRepository.findAll(Sort.by("username").ascending()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserManagementResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        return toResponse(user);
    }

    @Transactional
    public void deactivateUser(UUID id, UUID adminUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        user.setStatus("DEACTIVATED");
        user.setUpdatedBy(adminUserId);
        userRepository.save(user);
        log.info("User deactivated: userId={}", id);
    }

    @Transactional
    public void unlockUser(UUID id, UUID adminUserId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setUpdatedBy(adminUserId);
        userRepository.save(user);
        log.info("User unlocked: userId={}", id);
    }

    private UserManagementResponse toResponse(User user) {
        return new UserManagementResponse(
                user.getId(), user.getUsername(), user.getEmail(),
                user.getFullName(), user.getRole(), user.getStatus(),
                user.getLastLoginAt(), user.getCreatedAt()
        );
    }
}
