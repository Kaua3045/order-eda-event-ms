package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;
import java.util.Optional;

public record OrderShippingDetails(
        String shippingId,
        String shippingCompany,
        String shippingType,
        BigDecimal cost
) {

    public static OrderShippingDetails create(
            final String aShippingId,
            final String aShippingCompany,
            final String aShippingType,
            final BigDecimal aCost
    ) {
        final var aDetails = new OrderShippingDetails(aShippingId, aShippingCompany, aShippingType, aCost);
        aDetails.validate();
        return aDetails;
    }

    public static OrderShippingDetails create(
            final String aShippingCompany,
            final String aShippingType
    ) {
        final var aDetails = new OrderShippingDetails(null, aShippingCompany, aShippingType, BigDecimal.ZERO);
        aDetails.validate();
        return aDetails;
    }

    public static OrderShippingDetails create(
            final String aShippingCompany,
            final String aShippingType,
            final BigDecimal aCost
    ) {
        final var aDetails = new OrderShippingDetails(null, aShippingCompany, aShippingType, aCost);
        aDetails.validate();
        return aDetails;
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
