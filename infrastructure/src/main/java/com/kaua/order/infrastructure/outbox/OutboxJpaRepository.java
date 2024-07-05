package com.kaua.order.infrastructure.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<OutboxJpaEntity, String> {
}
