package com.kaua.order.domain.order.events.external;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record ShippingCostCalculatedEvent(
        String aggregateId,
        String orderStatus,
        BigDecimal totalAmountWithoutShipping,
        OrderAddress shippingAddress,
        OrderShippingDetails shippingDetails,
        String eventId,
        String eventType,
        String eventClassName,
        Instant occurredOn,
        long aggregateVersion,
        String who,
        String traceId
) implements DomainEvent {

    public static final String EVENT_TYPE = "ShippingCostCalculatedEvent";

    private ShippingCostCalculatedEvent(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderAddress shippingAddress,
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
                shippingDetails,
                IdUtils.generateIdWithoutHyphen(),
                EVENT_TYPE,
                ShippingCostCalculatedEvent.class.getName(),
                InstantUtils.now(),
                aggregateVersion,
                who,
                traceId // traceId in case of distributed tracing
        );
    }

    public static ShippingCostCalculatedEvent from(
            final String orderId,
            final String orderStatus,
            final BigDecimal totalAmount,
            final OrderAddress shippingAddress,
            final OrderShippingDetails shippingDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        return new ShippingCostCalculatedEvent(
                orderId,
                orderStatus,
                totalAmount,
                shippingAddress,
                shippingDetails,
                aggregateVersion,
                who,
                traceId
        );
    }
}
