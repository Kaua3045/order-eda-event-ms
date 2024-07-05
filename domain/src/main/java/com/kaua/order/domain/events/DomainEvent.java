package com.kaua.order.domain.events;

import java.time.Instant;

public interface DomainEvent {

    String aggregateId();

    String eventId();

    String eventType();

    Instant occurredOn();

    long aggregateVersion();

    String who();

    String traceId();
}
