package com.kaua.order.infrastructure.models;

public record OrderItemRequest(
        String sku,
        int quantity
) {

    public static OrderItemRequest with(
            final String aSku,
            final int aQuantity
    ) {
        return new OrderItemRequest(aSku, aQuantity);
    }
}
