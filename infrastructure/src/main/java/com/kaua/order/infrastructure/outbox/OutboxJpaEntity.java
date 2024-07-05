package com.kaua.order.infrastructure.outbox;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.infrastructure.configurations.json.Json;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "outbox")
public class OutboxJpaEntity {

    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "aggregate_version", nullable = false)
    private long aggregateVersion;

    @Column(name = "occurred_on", columnDefinition = "DATETIME(6)", nullable = false)
    private Instant occurredOn;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxStatus status;

    public OutboxJpaEntity() {}

    public OutboxJpaEntity(
            final String eventId,
            final String eventType,
            final String aggregateId,
            final long aggregateVersion,
            final Instant occurredOn,
            final String payload,
            final OutboxStatus status
    ) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
        this.occurredOn = occurredOn;
        this.payload = payload;
        this.status = status;
    }

    public static OutboxJpaEntity create(final DomainEvent aEvent) {
        return new OutboxJpaEntity(
                aEvent.eventId(),
                aEvent.eventType(),
                aEvent.aggregateId(),
                aEvent.aggregateVersion(),
                aEvent.occurredOn(),
                Json.writeValueAsString(aEvent),
                OutboxStatus.PENDING
        );
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public long getAggregateVersion() {
        return aggregateVersion;
    }

    public void setAggregateVersion(long aggregateVersion) {
        this.aggregateVersion = aggregateVersion;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }

    public void setOccurredOn(Instant occurredOn) {
        this.occurredOn = occurredOn;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public void setStatus(OutboxStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OutboxJpaEntity(" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateVersion=" + aggregateVersion +
                ", occurredOn=" + occurredOn +
                ", payload='" + payload + '\'' +
                ", status=" + status +
                ')';
    }
}
