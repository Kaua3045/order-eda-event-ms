package com.kaua.order.domain.order.events;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderPaymentTaxCalculatedEvent(
        String aggregateId,
        String orderStatus,
        BigDecimal totalAmount,
        OrderPaymentDetails paymentDetails,
        String eventId,
        String eventType,
        String eventClassName,
        Instant occurredOn,
        long aggregateVersion,
        String who,
        String traceId
) implements DomainEvent {

    public static final String EVENT_TYPE = "OrderPaymentTaxCalculatedEvent";

    private OrderPaymentTaxCalculatedEvent(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderPaymentDetails paymentDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        this(
                orderId,
                orderStatus,
                totalAmount,
                paymentDetails,
                IdUtils.generateIdWithoutHyphen(),
                EVENT_TYPE,
                OrderPaymentTaxCalculatedEvent.class.getName(),
                InstantUtils.now(),
                aggregateVersion,
                who,
                traceId // traceId in case of distributed tracing
        );
    }

    public static OrderPaymentTaxCalculatedEvent from(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderPaymentDetails paymentDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        return new OrderPaymentTaxCalculatedEvent(
                orderId,
                orderStatus,
                totalAmount,
                paymentDetails,
                aggregateVersion,
                who,
                traceId
        );
    }
}
