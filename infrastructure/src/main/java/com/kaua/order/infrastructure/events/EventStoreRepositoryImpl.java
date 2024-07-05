package com.kaua.order.infrastructure.events;

import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.infrastructure.events.persistence.EventsJpaEntity;
import com.kaua.order.infrastructure.events.persistence.EventsJpaRepository;
import com.kaua.order.infrastructure.exceptions.EventStoreException;
import com.kaua.order.infrastructure.outbox.OutboxJpaEntity;
import com.kaua.order.infrastructure.outbox.OutboxJpaRepository;
import com.kaua.order.infrastructure.transaction.TransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class EventStoreRepositoryImpl implements EventStore {

    private static final String HANDLE_CONCURRENCY_QUERY = "SELECT aggregate_id FROM events e WHERE e.aggregate_id = :aggregate_id LIMIT 1 FOR UPDATE";

    private static final Logger log = LoggerFactory.getLogger(EventStoreRepositoryImpl.class);

    private final EventsJpaRepository eventsRepository;
    private final OutboxJpaRepository outboxRepository;
    private final TransactionManager transactionManager;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EventStoreRepositoryImpl(
            final EventsJpaRepository eventsRepository,
            final OutboxJpaRepository outboxRepository,
            final TransactionManager transactionManager, NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.eventsRepository = Objects.requireNonNull(eventsRepository);
        this.outboxRepository = Objects.requireNonNull(outboxRepository);
        this.transactionManager = Objects.requireNonNull(transactionManager);
        this.jdbcTemplate = jdbcTemplate;
    }

    private <T extends DomainEvent> void store(List<T> events) {
        log.debug("Storing {} events", events.size());

        final var aTransactionResult = this.transactionManager.execute(() -> {
            log.debug("Creating events and outbox entities");

            final var aEventsJpaEntity = events.stream()
                    .map(EventsJpaEntity::with)
                    .toList();
            final var aOutboxJpaEntity = events.stream()
                    .map(OutboxJpaEntity::create)
                    .toList();

            log.debug("Saving events and outbox entities");
            this.eventsRepository.saveAll(aEventsJpaEntity);
            this.outboxRepository.saveAll(aOutboxJpaEntity);
            log.info("Events {} and outbox {} entities stored", aEventsJpaEntity.size(), aOutboxJpaEntity.size());
            return true;
        });

        if (aTransactionResult.isFailure()) {
            log.error("Error storing events");
            throw EventStoreException.with(aTransactionResult.getErrorResult().message());
        }
    }

    @Override
    public <T extends AggregateRoot<?>> void save(T aggregate) {
        log.debug("Storing events for aggregate {}", aggregate.getId().getValue());

        if (aggregate.getVersion() > 0) {
            log.debug("Handling concurrency for aggregate {}, actual version {}",
                    aggregate.getId().getValue(), aggregate.getVersion());
            this.handleConcurrency(aggregate.getId().getValue());
        }

        this.store(aggregate.getDomainEvents());
        // todo: check to store snapshot

        log.info("Aggregate {} stored {}", aggregate.getId().getValue(), aggregate);
    }

    private void handleConcurrency(final String aggregateId) {
        try {
            log.debug("Locking aggregateId {}", aggregateId);
            final var aAggregateId = this.jdbcTemplate.queryForObject(
                    HANDLE_CONCURRENCY_QUERY,
                    Map.of("aggregate_id", aggregateId),
                    String.class
            );
            log.info("AggregateId {} locked", aAggregateId);
        } catch (Exception e) {
            log.error("Error handling concurrency for aggregateId {}", aggregateId, e);
            throw EventStoreException.with("Error handling concurrency for aggregateId " + aggregateId);
        }
    }
}
