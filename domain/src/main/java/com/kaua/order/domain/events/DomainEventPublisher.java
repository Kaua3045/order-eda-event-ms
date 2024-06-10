package com.kaua.order.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event);
}
