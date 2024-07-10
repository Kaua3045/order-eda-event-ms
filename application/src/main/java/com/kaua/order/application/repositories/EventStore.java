package com.kaua.order.application.repositories;

import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.events.DomainEvent;

import java.util.List;

public interface EventStore {

    <T extends AggregateRoot<?>> void save(T aggregateRoot);

    <T extends DomainEvent> List<T> loadEvents(String aggregateId);
}
