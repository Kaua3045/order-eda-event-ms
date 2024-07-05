package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.ValueObject;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

public class OrderCoupon extends ValueObject {

    private final String code;
    private final float percentage;

    private OrderCoupon(final String aCode, final float aPercentage) {
        this.code = aCode;
        this.percentage = aPercentage;
        validate();
    }

    public static OrderCoupon create(final String aCode, final float aPercentage) {
        return new OrderCoupon(aCode, aPercentage);
    }

    public String getCode() {
        return this.code;
    }

    public float getPercentage() {
        return this.percentage;
    }

    private void validate() {
        final var aNotificationHandler = NotificationHandler.create();

        if (this.code == null || this.code.isBlank()) {
            aNotificationHandler.append(new Error("'code' should not be null or empty"));
        }
        if (this.percentage < 0) {
            aNotificationHandler.append(new Error("'percentage' should be greater than zero"));
        }

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to create OrderCoupon", aNotificationHandler);
        }
    }

    @Override
    public String toString() {
        return "OrderCoupon(" +
                "code='" + code + '\'' +
                ", percentage=" + percentage +
                ')';
    }
}
