package com.hospital.resource.auth.service;

import com.hospital.resource.auth.entity.PasswordHistory;
import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.PasswordHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,128}$"
    );

    public boolean isPasswordValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean isPasswordReused(User user, String newPassword) {
        List<PasswordHistory> history = passwordHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc(user.getId());
        return history.stream()
                .anyMatch(entry -> passwordEncoder.matches(newPassword, entry.getPasswordHash()));
    }

    @Transactional
    public void recordPasswordChange(User user, String newPassword) {
        String hash = encodePassword(newPassword);
        PasswordHistory history = PasswordHistory.builder()
                .user(user)
                .passwordHash(hash)
                .build();
        passwordHistoryRepository.save(history);
        passwordHistoryRepository.deleteOldestIfExceedsFive(user.getId());
    }
}
