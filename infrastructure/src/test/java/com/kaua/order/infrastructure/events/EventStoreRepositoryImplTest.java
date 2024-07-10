package com.kaua.order.infrastructure.events;

import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.Identifier;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.validation.ValidationHandler;
import com.kaua.order.infrastructure.DatabaseRepositoryTest;
import com.kaua.order.infrastructure.events.persistence.EventsJpaRepository;
import com.kaua.order.infrastructure.exceptions.EventStoreException;
import com.kaua.order.infrastructure.outbox.OutboxJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@DatabaseRepositoryTest
public class EventStoreRepositoryImplTest {

    @Autowired
    private EventStoreRepositoryImpl eventStoreRepository;

    @Autowired
    private EventsJpaRepository eventsJpaRepository;

    @Autowired
    private OutboxJpaRepository outboxJpaRepository;

    @Test
    void givenAValidDomainEvent_whenCallSave_thenShouldStoreEvent() {
        final var aAggregate = createAggregate(IdUtils.generateIdWithoutHyphen(), 0);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());
        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        Assertions.assertDoesNotThrow(() -> this.eventStoreRepository.save(aAggregate));

        Assertions.assertEquals(1, this.eventsJpaRepository.count());
        Assertions.assertEquals(1, this.outboxJpaRepository.count());
    }

    @Test
    void givenAValidAggregate_whenCallSave_thenShouldStoreEventAndHandleConcurrency() throws InterruptedException {
        final var aAggregate = createAggregate(IdUtils.generateIdWithoutHyphen(), 0);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());
        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        this.eventStoreRepository.save(aAggregate);

        Assertions.assertEquals(1, this.eventsJpaRepository.count());

        this.eventStoreRepository.save(createAggregate(aAggregate.getId().getValue(), 1));

        Assertions.assertEquals(2, this.eventsJpaRepository.count());
    }

    @Test
    void givenAValidAggregate_whenCallSave_thenShouldThrowExceptionBecauseLockFailure() throws InterruptedException {
        final var aAggregate = createAggregate(IdUtils.generateIdWithoutHyphen(), 0);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());
        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        this.eventStoreRepository.save(aAggregate);

        Assertions.assertEquals(1, this.eventsJpaRepository.count());

        doSyncAndConcurrently(
                2,
                s -> this.eventStoreRepository.save(createAggregate(aAggregate.getId().getValue(), 1)),
                "EventStoreRepositoryImplTest",
                0,
                2
        );

        Assertions.assertEquals(1, this.eventsJpaRepository.count());
    }

    @Test
    void givenAnInvalidDomainEvent_whenCallSave_thenShouldThrowException() {
        final var aAggregate = createAggregate(null, 0);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());
        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        Assertions.assertThrows(EventStoreException.class,
                () -> this.eventStoreRepository.save(aAggregate));
    }

    @Test
    void givenAValidAggregateId_whenCallLoadEvents_thenShouldReturnEventsForAggregateId() {
        final var aAggregate = createAggregate(IdUtils.generateIdWithoutHyphen(), 0);
        aAggregate.registerEvent(Fixture.sampleEntityEvent(aAggregate.getId().getValue(), 1));

        Assertions.assertEquals(0, this.eventsJpaRepository.count());

        this.eventStoreRepository.save(aAggregate);

        Assertions.assertEquals(2, this.eventsJpaRepository.count());

        final var aEvents = this.eventStoreRepository.loadEvents(aAggregate.getId().getValue());

        Assertions.assertEquals(2, aEvents.size());
    }

    @Test
    void givenAnInvalidEventClassNameInEvent_whenCallLoadEvents_thenShouldThrowException() {
        final var aAggregate = createAggregate(IdUtils.generateIdWithoutHyphen(), 0);
        final var aAggregateId = aAggregate.getId().value;
        aAggregate.registerEvent(Fixture.sampleEntityEvent(aAggregateId, 1));

        Assertions.assertEquals(0, this.eventsJpaRepository.count());

        this.eventStoreRepository.save(aAggregate);

        Assertions.assertEquals(2, this.eventsJpaRepository.count());

        this.eventsJpaRepository.findAll().forEach(e -> e.setEventClassName("InvalidClassName"));

        this.eventsJpaRepository.saveAll(this.eventsJpaRepository.findAll());

        Assertions.assertThrows(EventStoreException.class,
                () -> this.eventStoreRepository.loadEvents(aAggregateId));
    }

    private void doSyncAndConcurrently(
            final int threadCount,
            final Consumer<String> operation,
            final String classTestedName,
            final int successCount,
            final int errorCount
    ) throws InterruptedException {
        final var startLatch = new CountDownLatch(1);
        final var endLatch = new CountDownLatch(threadCount);
        final var aSuccessCount = new AtomicInteger(0);
        final var aErrorCount = new AtomicInteger(0);

        if (threadCount <= 0 || threadCount > 10) {
            throw new RuntimeException("Thread count must be between 1 and 10");
        }

        final var aThreadPrefix = classTestedName + "-";
        final var aExecutorService = Executors.newFixedThreadPool(threadCount, new CustomizableThreadFactory(
                aThreadPrefix
        ));

        for (int i = 0; i < threadCount; i++) {
            String threadName = "Thread " + i;
            aExecutorService.execute(() -> {
                try {
                    startLatch.await();
                    operation.accept(threadName);
                    aSuccessCount.incrementAndGet();
                } catch (Exception e) {
                    aErrorCount.incrementAndGet();
                    System.out.println("Error in thread " + threadName + ": " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
        aExecutorService.shutdown();

        Assertions.assertEquals(successCount, aSuccessCount.get());
        Assertions.assertEquals(errorCount, aErrorCount.get());
    }

    private AggregateRoot<SampleIdentifier> createAggregate(final String id, final long version) {
        SampleIdentifier sampleId = new SampleIdentifier(id);
        AggregateRoot<SampleIdentifier> aggregateRoot = new AggregateRoot<>(sampleId, version, Collections.emptyList()) {
            @Override
            public void validate(ValidationHandler aHandler) {

            }
        };

        aggregateRoot.registerEvent(Fixture.sampleEntityEvent(aggregateRoot.getId().getValue(), aggregateRoot.getVersion()));

        return aggregateRoot;
    }

    private static class SampleIdentifier extends Identifier {
        private final String value;

        public SampleIdentifier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

}
