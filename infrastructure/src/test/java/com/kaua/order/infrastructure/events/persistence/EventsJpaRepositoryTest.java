package com.kaua.order.infrastructure.events.persistence;

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
public class EventsJpaRepositoryTest {

    @Autowired
    private EventsJpaRepository eventsJpaRepository;

    @Test
    void givenAValidDomainEvent_whenCallSave_thenShouldSaveOutboxJpaEntity() {
        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);
        final var aEventEntity = EventsJpaEntity.with(aDomainEvent);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());

        final var aSavedEventJpaEntity = this.eventsJpaRepository.save(aEventEntity);

        Assertions.assertEquals(1, this.eventsJpaRepository.count());

        Assertions.assertEquals(aEventEntity.getEventId(), aSavedEventJpaEntity.getEventId());
        Assertions.assertEquals(aEventEntity.getEventType(), aSavedEventJpaEntity.getEventType());
        Assertions.assertEquals(aEventEntity.getAggregateId(), aSavedEventJpaEntity.getAggregateId());
        Assertions.assertEquals(aEventEntity.getAggregateVersion(), aSavedEventJpaEntity.getAggregateVersion());
        Assertions.assertEquals(aEventEntity.getOccurredOn(), aSavedEventJpaEntity.getOccurredOn());
        Assertions.assertEquals(aEventEntity.getPayload(), aSavedEventJpaEntity.getPayload());
        Assertions.assertNotNull(aSavedEventJpaEntity.toString());
    }

    @Test
    void givenAnInvalidNullEventType_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "eventType";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.events.persistence.EventsJpaEntity.eventType";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setEventType(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventsJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullAggregateId_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "aggregateId";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.events.persistence.EventsJpaEntity.aggregateId";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setAggregateId(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventsJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAValidAggregateVersion_whenCallSave_shouldReturnAnOutboxEntity() {
        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setAggregateVersion(2);

        Assertions.assertEquals(0, this.eventsJpaRepository.count());

        final var aSavedEventJpaEntity = this.eventsJpaRepository.save(aEntity);

        Assertions.assertEquals(1, this.eventsJpaRepository.count());

        Assertions.assertEquals(aEntity.getEventId(), aSavedEventJpaEntity.getEventId());
        Assertions.assertEquals(aEntity.getEventType(), aSavedEventJpaEntity.getEventType());
        Assertions.assertEquals(aEntity.getAggregateId(), aSavedEventJpaEntity.getAggregateId());
        Assertions.assertEquals(aEntity.getAggregateVersion(), aSavedEventJpaEntity.getAggregateVersion());
        Assertions.assertEquals(aEntity.getOccurredOn(), aSavedEventJpaEntity.getOccurredOn());
        Assertions.assertEquals(aEntity.getPayload(), aSavedEventJpaEntity.getPayload());
    }

    @Test
    void givenAnInvalidNullOccurredOn_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "occurredOn";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.events.persistence.EventsJpaEntity.occurredOn";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setOccurredOn(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventsJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullPayload_whenCallSave_shouldReturnAnException() {
        final var expectedPropertyName = "payload";
        final var expectedErrorMessage = "not-null property references a null or transient value : com.kaua.order.infrastructure.events.persistence.EventsJpaEntity.payload";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setPayload(null);

        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> eventsJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedPropertyName, actualCause.getPropertyName());
        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }

    @Test
    void givenAnInvalidNullEventId_whenCallSave_shouldReturnAnException() {
        final var expectedErrorMessage = "Identifier of entity 'com.kaua.order.infrastructure.events.persistence.EventsJpaEntity' must be manually assigned before calling 'persist()'";

        final var aDomainEvent = Fixture.sampleEntityEvent(IdUtils.generateIdWithHyphen(), 0);

        final var aEntity = EventsJpaEntity.with(aDomainEvent);
        aEntity.setEventId(null);

        final var actualException = Assertions.assertThrows(JpaSystemException.class,
                () -> eventsJpaRepository.save(aEntity));

        final var actualCause = Assertions.assertInstanceOf(IdentifierGenerationException.class,
                actualException.getCause());

        Assertions.assertEquals(expectedErrorMessage, actualCause.getMessage());
    }
}
