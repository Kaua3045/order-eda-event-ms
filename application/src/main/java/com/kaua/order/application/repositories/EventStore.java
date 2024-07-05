package com.kaua.order.application.repositories;

import com.kaua.order.domain.AggregateRoot;

public interface EventStore {

    <T extends AggregateRoot<?>> void save(T aggregateRoot);
}
