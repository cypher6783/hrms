package com.hospital.resource.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(DomainEvent event) {
        log.info("Publishing domain event: type={}, eventId={}", event.getEventType(), event.getEventId());
        eventPublisher.publishEvent(event);
    }
}
