package com.kaua.order.domain.order.events;

import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.OrderItem;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderCoupon;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record OrderCreationInitiatedEvent(
        String aggregateId,
        String orderStatus,
        String customerId,
        BigDecimal totalAmount,
        Set<OrderItem> items,
        OrderAddress shippingAddress,
        OrderCoupon coupon,
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

    public static final String EVENT_TYPE = "OrderCreationInitiatedEvent";

    private OrderCreationInitiatedEvent(
            final String orderId,
            final String orderStatus,
            final String customerId,
            final BigDecimal totalAmount,
            final Set<OrderItem> items,
            final OrderAddress shippingAddress,
            final OrderCoupon coupon,
            final OrderPaymentDetails paymentDetails,
            final OrderShippingDetails shippingDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        this(
                orderId,
                orderStatus,
                customerId,
                totalAmount,
                items,
                shippingAddress,
                coupon,
                paymentDetails,
                shippingDetails,
                IdUtils.generateIdWithoutHyphen(),
                EVENT_TYPE,
                OrderCreationInitiatedEvent.class.getName(),
                InstantUtils.now(),
                aggregateVersion,
                who,
                traceId // traceId in case of distributed tracing
        );
    }

    public static OrderCreationInitiatedEvent from(
            final String orderId,
            final String orderStatus,
            final String customerId,
            final BigDecimal totalAmount,
            final Set<OrderItem> items,
            final OrderAddress shippingAddress,
            final OrderCoupon coupon,
            final OrderPaymentDetails paymentDetails,
            final OrderShippingDetails shippingDetails,
            final long aggregateVersion,
            final String who,
            final String traceId
    ) {
        return new OrderCreationInitiatedEvent(
                orderId,
                orderStatus,
                customerId,
                totalAmount,
                items,
                shippingAddress,
                coupon,
                paymentDetails,
                shippingDetails,
                aggregateVersion,
                who,
                traceId
        );
    }
}
