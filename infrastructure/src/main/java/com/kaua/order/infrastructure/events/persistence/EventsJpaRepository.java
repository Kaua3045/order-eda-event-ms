package com.kaua.order.infrastructure.events.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsJpaRepository extends JpaRepository<EventsJpaEntity, String> {
}
