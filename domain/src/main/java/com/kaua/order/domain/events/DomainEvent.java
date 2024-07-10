package com.kaua.order.domain.events;

import java.io.Serializable;
import java.time.Instant;

public interface DomainEvent extends Serializable {

    String aggregateId();

    String eventId();

    String eventType();

    String eventClassName();

    Instant occurredOn();

    long aggregateVersion();

    String who();

    String traceId();
}
