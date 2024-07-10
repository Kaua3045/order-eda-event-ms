package com.kaua.order.domain.order.events;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderShippingCostCalculatedEvent(
        String aggregateId,
        String orderStatus,
        BigDecimal totalAmount,
        OrderAddress shippingAddress,
        OrderPaymentDetails paymentDetails,
        OrderShippingDetails shippingDetails,
        String eventId,
        String eventType,
        String eventClassName,
        Instant occurredOn,
        long aggregateVersion,
        String who,
        String traceId
) implements DomainEvent {

    public static final String EVENT_TYPE = "OrderShippingCostCalculatedEvent";

    private OrderShippingCostCalculatedEvent(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderAddress shippingAddress,
            final OrderPaymentDetails paymentDetails,
            final OrderShippingDetails shippingDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        this(
                orderId,
                orderStatus,
                totalAmount,
                shippingAddress,
                paymentDetails,
                shippingDetails,
                IdUtils.generateIdWithoutHyphen(),
                EVENT_TYPE,
                OrderShippingCostCalculatedEvent.class.getName(),
                InstantUtils.now(),
                aggregateVersion,
                who,
                traceId // traceId in case of distributed tracing
        );
    }

    public static OrderShippingCostCalculatedEvent from(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderAddress shippingAddress,
            final OrderPaymentDetails paymentDetails,
            final OrderShippingDetails shippingDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        return new OrderShippingCostCalculatedEvent(
                orderId,
                orderStatus,
                totalAmount,
                shippingAddress,
                paymentDetails,
                shippingDetails,
                aggregateVersion,
                who,
                traceId
        );
    }
}
