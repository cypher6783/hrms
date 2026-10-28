package com.hospital.resource.common.event;

import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent extends ApplicationEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    protected DomainEvent(Object source, String eventType) {
        super(source);
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = eventType;
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getEventType() {
        return eventType;
    }
}
