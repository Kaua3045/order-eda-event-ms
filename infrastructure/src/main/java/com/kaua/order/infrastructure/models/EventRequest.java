package com.kaua.order.infrastructure.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.order.events.external.PaymentTaxCalculatedEvent;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.infrastructure.configurations.json.Json;
import com.kaua.order.infrastructure.constants.HeadersConstants;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.math.BigDecimal;
import java.util.Map;

public record EventRequest(
        @JsonProperty("type") String type,
        @JsonProperty("topic") String topic,
        @JsonProperty("payload") Map<String, Object> payload
) {

    public EventRequest {
        if (type == null || type.isBlank()) {
            throw DomainException.with(new Error("'type' should not be null or empty"));
        }
        if (topic == null || topic.isBlank()) {
            throw DomainException.with(new Error("'topic' should not be null or empty"));
        }
        if (payload == null || payload.isEmpty()) {
            throw DomainException.with(new Error("'payload' should not be null or empty"));
        }
    }

    public ProducerRecord<String, Object> toProducerRecord() {
        return switch (type) {
            case ShippingCostCalculatedEvent.EVENT_TYPE -> {
                final var anEvent = ShippingCostCalculatedEvent.from(
                        payload.get("orderId").toString(),
                        payload.get("orderStatus").toString(),
                        new BigDecimal(payload.get("totalAmount").toString()),
                        OrderAddress.create(
                                payload.get("street").toString(),
                                payload.get("number").toString(),
                                payload.get("complement").toString(),
                                payload.get("city").toString(),
                                payload.get("state").toString(),
                                payload.get("zipCode").toString()
                        ),
                        OrderShippingDetails.create(
                                payload.get("shippingCompany").toString(),
                                payload.get("shippingType").toString(),
                                new BigDecimal(payload.get("shippingCost").toString())
                        ),
                        Long.parseLong(payload.get("aggregateVersion").toString()),
                        payload.get("who").toString(),
                        IdUtils.generateIdWithoutHyphen() // in future use x-idempotency-key or x-request-id
                );
                final var aEventSerialized = Json.writeValueAsString(anEvent);
                final var aProducerRecord = new ProducerRecord<String, Object>(topic, aEventSerialized);
                aProducerRecord.headers().add(HeadersConstants.EVENT_ID, anEvent.eventId().getBytes());
                aProducerRecord.headers().add(HeadersConstants.EVENT_TYPE, anEvent.eventType().getBytes());
                aProducerRecord.headers().add(HeadersConstants.EVENT_OCCURRED_ON, anEvent.occurredOn().toString().getBytes());
                aProducerRecord.headers().add(HeadersConstants.WHO, anEvent.who().getBytes());
                aProducerRecord.headers().add(HeadersConstants.TRACE_ID, anEvent.traceId().getBytes());
                yield aProducerRecord;
            }
            case PaymentTaxCalculatedEvent.EVENT_TYPE -> {
                final var anEvent = PaymentTaxCalculatedEvent.from(
                        payload.get("orderId").toString(),
                        payload.get("orderStatus").toString(),
                        new BigDecimal(payload.get("totalAmount").toString()),
                        OrderPaymentDetails.create(
                                payload.get("paymentMethodId").toString(),
                                Integer.parseInt(payload.get("installments").toString()),
                                new BigDecimal(payload.get("paymentTax").toString())
                        ),
                        Long.parseLong(payload.get("aggregateVersion").toString()),
                        payload.get("who").toString(),
                        IdUtils.generateIdWithoutHyphen() // in future use x-idempotency-key or x-request-id
                );
                final var aEventSerialized = Json.writeValueAsString(anEvent);
                final var aProducerRecord = new ProducerRecord<String, Object>(topic, aEventSerialized);
                aProducerRecord.headers().add(HeadersConstants.EVENT_ID, anEvent.eventId().getBytes());
                aProducerRecord.headers().add(HeadersConstants.EVENT_TYPE, anEvent.eventType().getBytes());
                aProducerRecord.headers().add(HeadersConstants.EVENT_OCCURRED_ON, anEvent.occurredOn().toString().getBytes());
                aProducerRecord.headers().add(HeadersConstants.WHO, anEvent.who().getBytes());
                aProducerRecord.headers().add(HeadersConstants.TRACE_ID, anEvent.traceId().getBytes());
                yield aProducerRecord;
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
