package com.kaua.order.domain.order;

import java.util.Arrays;
import java.util.Optional;

public enum OrderStatus {

    CREATION_INITIATED,
    SHIPPING_CALCULATED,
    PAYMENT_TAX_CALCULATED,
    CREATED,
    STOCK_CONFIRMED,
    STOCK_CONFIRMATION_FAILED,
    PAYMENT_INITIATED,
    PAYMENT_PENDING,
    PAYMENT_APPROVED,
    PAYMENT_DENIED,
    PAYMENT_REFUNDED,
    PREPARING_FOR_SHIPPING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    DELIVERY_FAILED,
    CANCELED,
    REFUNDED,
    RETURN_REQUESTED,
    RETURNED;

    public static Optional<OrderStatus> of(final String aStatus) {
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(aStatus))
                .findFirst();
    }
}
