package com.kaua.order.application.handlers.commands;

public record CreateOrderItemCommand(
        String sku,
        int quantity
) {

    public static CreateOrderItemCommand with(
            final String aSku,
            final int aQuantity
    ) {
        return new CreateOrderItemCommand(aSku, aQuantity);
    }
}
