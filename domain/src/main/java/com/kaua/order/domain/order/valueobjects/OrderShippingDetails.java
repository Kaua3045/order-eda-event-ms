package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.ValueObject;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;
import java.util.Optional;

public class OrderShippingDetails extends ValueObject {

    private final String shippingId;
    private final String shippingCompany;
    private final String shippingType;
    private final BigDecimal cost;

    private OrderShippingDetails(
            final String aShippingId,
            final String aShippingCompany,
            final String aShippingType,
            final BigDecimal aCost
    ) {
        this.shippingId = aShippingId;
        this.shippingCompany = aShippingCompany;
        this.shippingType = aShippingType;
        this.cost = aCost;
        validate();
    }

    public static OrderShippingDetails create(
            final String aShippingId,
            final String aShippingCompany,
            final String aShippingType,
            final BigDecimal aCost
    ) {
        return new OrderShippingDetails(aShippingId, aShippingCompany, aShippingType, aCost);
    }

    public static OrderShippingDetails create(
            final String aShippingCompany,
            final String aShippingType
    ) {
        return new OrderShippingDetails(null, aShippingCompany, aShippingType, BigDecimal.ZERO);
    }

    public static OrderShippingDetails create(
            final String aShippingCompany,
            final String aShippingType,
            final BigDecimal aCost
    ) {
        return new OrderShippingDetails(null, aShippingCompany, aShippingType, aCost);
    }

    public Optional<String> getShippingId() {
        return Optional.ofNullable(shippingId);
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public String getShippingType() {
        return shippingType;
    }

    public BigDecimal getCost() {
        return cost;
    }

    private void validate() {
        final var aNotificationHandler = NotificationHandler.create();

        if (getShippingId().isPresent() && this.shippingId.isBlank()) {
            aNotificationHandler.append(new Error("'shippingId' should not be empty"));
        }
        if (this.shippingCompany == null || this.shippingCompany.isBlank()) {
            aNotificationHandler.append(new Error("'shippingCompany' should not be null or empty"));
        }
        if (this.shippingType == null || this.shippingType.isBlank()) {
            aNotificationHandler.append(new Error("'shippingType' should not be null or empty"));
        }

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to create OrderShippingDetails", aNotificationHandler);
        }
    }

    @Override
    public String toString() {
        return "OrderShippingDetails{" +
                "shippingId='" + getShippingId().orElse(null) + '\'' +
                ", shippingCompany='" + shippingCompany + '\'' +
                ", shippingType='" + shippingType + '\'' +
                ", cost=" + cost +
                '}';
    }
}
