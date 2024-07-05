package com.kaua.order.domain.order;

import com.kaua.order.domain.ValueObject;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;

public class OrderItem extends ValueObject {

    private final String orderItemId;
    private final String sku;
    private final int quantity;
    private final BigDecimal unitAmount;
    private final BigDecimal totalAmount;

    private OrderItem(
            final String aOrderItemId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aUnitAmount,
            final BigDecimal aTotalAmount
    ) {
        this.orderItemId = aOrderItemId;
        this.sku = aSku;
        this.quantity = aQuantity;
        this.unitAmount = aUnitAmount;
        this.totalAmount = aTotalAmount;
        validate();
    }

    public static OrderItem create(
            final String aSku,
            final int aQuantity,
            final BigDecimal aUnitAmount
    ) {
        final String orderItemId = IdUtils.generateIdWithoutHyphen();
        final BigDecimal totalAmount = aUnitAmount.multiply(BigDecimal.valueOf(aQuantity));
        return new OrderItem(orderItemId, aSku, aQuantity, aUnitAmount, totalAmount);
    }

    public static OrderItem with(
            final String aOrderItemId,
            final String aSku,
            final int aQuantity,
            final BigDecimal aUnitAmount,
            final BigDecimal aTotalAmount
    ) {
        return new OrderItem(aOrderItemId, aSku, aQuantity, aUnitAmount, aTotalAmount);
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
