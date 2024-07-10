package com.kaua.order.domain.order;

import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;

public record OrderItem(
        String orderItemId,
        String sku,
        int quantity,
        BigDecimal unitAmount,
        BigDecimal totalAmount
) {

    public static OrderItem create(
            final String aSku,
            final int aQuantity,
            final BigDecimal aUnitAmount
    ) {
        final String orderItemId = IdUtils.generateIdWithoutHyphen();
        final BigDecimal totalAmount = aUnitAmount.multiply(BigDecimal.valueOf(aQuantity));
        final var aOrderItem = new OrderItem(orderItemId, aSku, aQuantity, aUnitAmount, totalAmount);
        aOrderItem.validate();
        return aOrderItem;
    }

    public static OrderItem with(
            final String aOrderItemId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aUnitAmount,
            final BigDecimal aTotalAmount
    ) {
        final var aOrderItem = new OrderItem(aOrderItemId, aSku, aQuantity, aUnitAmount, aTotalAmount);
        aOrderItem.validate();
        return aOrderItem;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitAmount() {
        return unitAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    private void validate() {
        final var aNotificationHandler = NotificationHandler.create();

        if (this.orderItemId == null || this.orderItemId.isBlank()) {
            aNotificationHandler.append(new Error("'orderItemId' should not be null or empty"));
        }
        if (this.sku == null || this.sku.isBlank()) {
            aNotificationHandler.append(new Error("'sku' should not be null or empty"));
        }
        if (this.quantity <= 0) {
            aNotificationHandler.append(new Error("'quantity' should be greater than 0"));
        }
        if (this.unitAmount == null || this.unitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            aNotificationHandler.append(new Error("'unitAmount' should be greater than 0"));
        }

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to create OrderItem", aNotificationHandler);
        }
    }
}
