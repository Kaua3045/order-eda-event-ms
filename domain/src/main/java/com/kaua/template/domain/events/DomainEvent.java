package com.kaua.template.domain.events;

import java.time.Instant;

public interface DomainEvent {

    String eventId(); // event identifier (UUID)

    String eventType(); // event type (OrderCreated, OrderUpdated, OrderDeleted, etc.)

    Instant occurredOn(); // event occurred date (2021-07-01T00:00:00Z)

    long aggregateVersion(); // event/aggregate version (1, 2, 3, etc.)

    String source(); // event source (OrderService, PaymentService, etc.) talvez não seja preciso

    String traceId(); // trace identifier (UUID) podemos colocar se quiser

    // podemos colocar o email ou name do user que gerou o evento
}
