package com.hospital.resource.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "login_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "username_attempted", nullable = false, length = 50)
    private String usernameAttempted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "event_type", nullable = false, length = 30)
    private String eventType;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(columnDefinition = "jsonb")
    private String details;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getUsernameAttempted() { return usernameAttempted; }
    public void setUsernameAttempted(String usernameAttempted) { this.usernameAttempted = usernameAttempted; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public static LoginAuditLogBuilder builder() { return new LoginAuditLogBuilder(); }

    public static class LoginAuditLogBuilder {
        private UUID id;
        private Instant timestamp;
        private String usernameAttempted;
        private User user;
        private String eventType;
        private String ipAddress;
        private String userAgent;
        private String details;

        LoginAuditLogBuilder() {}

        public LoginAuditLogBuilder id(UUID id) { this.id = id; return this; }
        public LoginAuditLogBuilder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public LoginAuditLogBuilder usernameAttempted(String usernameAttempted) { this.usernameAttempted = usernameAttempted; return this; }
        public LoginAuditLogBuilder user(User user) { this.user = user; return this; }
        public LoginAuditLogBuilder eventType(String eventType) { this.eventType = eventType; return this; }
        public LoginAuditLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public LoginAuditLogBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public LoginAuditLogBuilder details(String details) { this.details = details; return this; }

        public LoginAuditLog build() {
            LoginAuditLog log = new LoginAuditLog();
            log.setId(id);
            log.setTimestamp(timestamp);
            log.setUsernameAttempted(usernameAttempted);
            log.setUser(user);
            log.setEventType(eventType);
            log.setIpAddress(ipAddress);
            log.setUserAgent(userAgent);
            log.setDetails(details);
            return log;
        }
    }
}
