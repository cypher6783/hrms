package com.hospital.resource.auth.service;

import com.hospital.resource.auth.dto.*;
import com.hospital.resource.auth.entity.LoginAuditLog;
import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.LoginAuditLogRepository;
import com.hospital.resource.auth.repository.UserRepository;
import com.hospital.resource.auth.security.JwtTokenProvider;
import com.hospital.resource.common.exception.UnauthorizedException;
import com.hospital.resource.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final PasswordService passwordService;
    private final LoginAuditLogRepository loginAuditLogRepository;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> {
                    logLoginAttempt(request.username(), null, "LOGIN_FAILURE", ipAddress, userAgent, "User not found");
                    throw new BadCredentialsException("Invalid credentials");
                });

        if (user.isLocked()) {
            logLoginAttempt(request.username(), user.getId(), "ACCOUNT_LOCKED", ipAddress, userAgent, "Account locked");
            throw new UnauthorizedException("Account is locked. Try again after " + LOCKOUT_DURATION.toMinutes() + " minutes");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException e) {
            handleFailedLogin(user, ipAddress, userAgent);
            throw e;
        }

        handleSuccessfulLogin(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        com.hospital.resource.auth.entity.RefreshToken refreshToken = tokenService.createRefreshToken(user);

        logLoginAttempt(request.username(), user.getId(), "LOGIN_SUCCESS", ipAddress, userAgent, null);

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                accessToken,
                refreshToken.getRawToken(),
                900000L // 15 minutes
        );
    }

    @Transactional
    public RefreshTokenResponse refresh(String rawRefreshToken) {
        Optional<com.hospital.resource.auth.entity.RefreshToken> existingToken =
                tokenService.validateRefreshToken(rawRefreshToken);

        if (existingToken.isEmpty()) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        com.hospital.resource.auth.entity.RefreshToken oldToken = existingToken.get();
        User user = oldToken.getUser();

        // Revoke old token
        tokenService.revokeRefreshToken(rawRefreshToken);

        // Generate new tokens
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        com.hospital.resource.auth.entity.RefreshToken newRefreshToken = tokenService.createRefreshToken(user);

        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken.getRawToken(),
                900000L
        );
    }

    @Transactional
    public void logout(String rawRefreshToken, String ipAddress, String userAgent) {
        tokenService.revokeRefreshToken(rawRefreshToken);
        log.info("User logged out, IP: {}", ipAddress);
    }

    private void handleSuccessfulLogin(User user) {
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }

    private void handleFailedLogin(User user, String ipAddress, String userAgent) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_LOGIN_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plus(LOCKOUT_DURATION));
            logLoginAttempt(user.getUsername(), user.getId(), "ACCOUNT_LOCKED", ipAddress, userAgent,
                    "Locked after " + attempts + " failed attempts");
        }

        userRepository.save(user);
        logLoginAttempt(user.getUsername(), user.getId(), "LOGIN_FAILURE", ipAddress, userAgent,
                "Failed attempt " + attempts);
    }

    private void logLoginAttempt(String username, UUID userId, String eventType, String ipAddress, String userAgent, String details) {
        LoginAuditLog auditLog = LoginAuditLog.builder()
                .usernameAttempted(username)
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .eventType(eventType)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .details(details)
                .build();
        loginAuditLogRepository.save(auditLog);
    }
}
