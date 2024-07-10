package com.kaua.order.infrastructure.events.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventsJpaRepository extends JpaRepository<EventsJpaEntity, String> {

    List<EventsJpaEntity> findByAggregateId(String aggregateId);
}
