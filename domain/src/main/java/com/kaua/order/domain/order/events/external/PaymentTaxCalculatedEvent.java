package com.kaua.order.domain.order.events.external;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentTaxCalculatedEvent(
        String aggregateId,
        String orderStatus,
        BigDecimal totalAmountWithoutTax,
        OrderPaymentDetails paymentDetails,
        String eventId,
        String eventType,
        String eventClassName,
        Instant occurredOn,
        long aggregateVersion,
        String who,
        String traceId
) implements DomainEvent {

    public static final String EVENT_TYPE = "PaymentTaxCalculatedEvent";

    private PaymentTaxCalculatedEvent(
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
                PaymentTaxCalculatedEvent.class.getName(),
                InstantUtils.now(),
                aggregateVersion,
                who,
                traceId // traceId in case of distributed tracing
        );
    }

    public static PaymentTaxCalculatedEvent from(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderPaymentDetails paymentDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        return new PaymentTaxCalculatedEvent(
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
