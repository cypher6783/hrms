package com.hospital.resource.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Boolean getRevoked() { return revoked; }
    public void setRevoked(Boolean revoked) { this.revoked = revoked; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public static RefreshTokenBuilder builder() { return new RefreshTokenBuilder(); }

    public static class RefreshTokenBuilder {
        private UUID id;
        private String tokenHash;
        private User user;
        private Instant expiresAt;
        private Boolean revoked = false;
        private Instant createdAt;
        private String rawToken;

        RefreshTokenBuilder() {}

        public RefreshTokenBuilder id(UUID id) { this.id = id; return this; }
        public RefreshTokenBuilder tokenHash(String tokenHash) { this.tokenHash = tokenHash; return this; }
        public RefreshTokenBuilder user(User user) { this.user = user; return this; }
        public RefreshTokenBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public RefreshTokenBuilder revoked(Boolean revoked) { this.revoked = revoked; return this; }
        public RefreshTokenBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public RefreshTokenBuilder rawToken(String rawToken) { this.rawToken = rawToken; return this; }

        public RefreshToken build() {
            RefreshToken token = new RefreshToken();
            token.setId(id);
            token.setTokenHash(tokenHash);
            token.setUser(user);
            token.setExpiresAt(expiresAt);
            token.setRevoked(revoked);
            token.setCreatedAt(createdAt);
            token.setRawToken(rawToken);
            return token;
        }
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return Boolean.FALSE.equals(revoked) && !isExpired();
    }

    @Transient
    private String rawToken;

    public String getRawToken() {
        return rawToken;
    }

    public void setRawToken(String rawToken) {
        this.rawToken = rawToken;
    }
}
