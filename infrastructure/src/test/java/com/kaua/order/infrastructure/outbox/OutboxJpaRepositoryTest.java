package com.kaua.order.infrastructure.outbox;

import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.infrastructure.IntegrationTest;
import org.hibernate.PropertyValueException;
import org.hibernate.id.IdentifierGenerationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;

@IntegrationTest
public class OutboxJpaRepositoryTest {

    @Autowired
    private OutboxJpaRepository outboxJpaRepository;

    @Test
    void givenAValidDomainEvent_whenCallSave_thenShouldSaveOutboxJpaEntity() {
        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);
        final var aOutboxJpaEntity = OutboxJpaEntity.create(aDomainEvent);

        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        final var aSavedOutboxJpaEntity = this.outboxJpaRepository.save(aOutboxJpaEntity);

        Assertions.assertEquals(1, this.outboxJpaRepository.count());

        Assertions.assertEquals(aOutboxJpaEntity.getEventId(), aSavedOutboxJpaEntity.getEventId());
        Assertions.assertEquals(aOutboxJpaEntity.getEventType(), aSavedOutboxJpaEntity.getEventType());
        Assertions.assertEquals(aOutboxJpaEntity.getAggregateId(), aSavedOutboxJpaEntity.getAggregateId());
        Assertions.assertEquals(aOutboxJpaEntity.getAggregateVersion(), aSavedOutboxJpaEntity.getAggregateVersion());
        Assertions.assertEquals(aOutboxJpaEntity.getOccurredOn(), aSavedOutboxJpaEntity.getOccurredOn());
        Assertions.assertEquals(aOutboxJpaEntity.getPayload(), aSavedOutboxJpaEntity.getPayload());
        Assertions.assertEquals(aOutboxJpaEntity.getStatus(), aSavedOutboxJpaEntity.getStatus());
        Assertions.assertNotNull(aSavedOutboxJpaEntity.toString());
    }

    @Test
    void givenAnInvalidNullEventType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "eventType";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.outbox.OutboxJpaEntity.eventType";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setEventType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullAggregateId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "aggregateId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.outbox.OutboxJpaEntity.aggregateId";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setAggregateId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidAggregateVersion_whenCallSave_shouldReturnAnOutboxEntity() {
        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setAggregateVersion(2);

        Assertions.assertEquals(0, this.outboxJpaRepository.count());

        final var aSavedOutboxJpaEntity = this.outboxJpaRepository.save(aEntity);

        Assertions.assertEquals(1, this.outboxJpaRepository.count());

        Assertions.assertEquals(aEntity.getEventId(), aSavedOutboxJpaEntity.getEventId());
        Assertions.assertEquals(aEntity.getEventType(), aSavedOutboxJpaEntity.getEventType());
        Assertions.assertEquals(aEntity.getAggregateId(), aSavedOutboxJpaEntity.getAggregateId());
        Assertions.assertEquals(aEntity.getAggregateVersion(), aSavedOutboxJpaEntity.getAggregateVersion());
        Assertions.assertEquals(aEntity.getOccurredOn(), aSavedOutboxJpaEntity.getOccurredOn());
        Assertions.assertEquals(aEntity.getPayload(), aSavedOutboxJpaEntity.getPayload());
        Assertions.assertEquals(aEntity.getStatus(), aSavedOutboxJpaEntity.getStatus());
    }

    @Test
    void givenAnInvalidNullOccurredOn_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "occurredOn";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.outbox.OutboxJpaEntity.occurredOn";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setOccurredOn(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullPayload_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "payload";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.outbox.OutboxJpaEntity.payload";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setPayload(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullStatus_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "status";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.outbox.OutboxJpaEntity.status";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setStatus(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullEventId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.order.infrastructure.outbox.OutboxJpaEntity' must be manually assigned before calling 'persist()'";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = OutboxJpaEntity.create(aDomainEvent);
        aEntity.setEventId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> outboxJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
