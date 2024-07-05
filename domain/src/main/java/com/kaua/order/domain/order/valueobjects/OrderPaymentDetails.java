package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.ValueObject;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;
import java.util.Optional;

public class OrderPaymentDetails extends ValueObject {

    private final String paymentId;
    private final String paymentMethodId;
    private final int installments;
    private final BigDecimal tax;

    private OrderPaymentDetails(
            final String aPaymentId,
            final String aPaymentMethodId,
            final int aInstallments,
            final BigDecimal aTax
    ) {
        this.paymentId = aPaymentId;
        this.paymentMethodId = aPaymentMethodId;
        this.installments = aInstallments;
        this.tax = aTax;
        validate();
    }

    public static OrderPaymentDetails create(
            final String aPaymentId,
            final String aPaymentMethodId,
            final int aInstallments,
            final BigDecimal aTax
    ) {
        return new OrderPaymentDetails(aPaymentId, aPaymentMethodId, aInstallments, aTax);
    }

    public static OrderPaymentDetails create(
            final String aPaymentMethodId,
            final int aInstallments,
            final BigDecimal aTax
    ) {
        return new OrderPaymentDetails(null, aPaymentMethodId, aInstallments, aTax);
    }

    public static OrderPaymentDetails create(
            final String aPaymentMethodId,
            final int aInstallments
    ) {
        return new OrderPaymentDetails(null, aPaymentMethodId, aInstallments, BigDecimal.ZERO);
    }

    public Optional<String> getPaymentId() {
        return Optional.ofNullable(paymentId);
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public int getInstallments() {
        return installments;
    }

    public BigDecimal getTax() {
        return tax;
    }

    private void validate() {
        final var aNotificationHandler = NotificationHandler.create();

        if (this.getPaymentId().isPresent() && this.paymentId.isBlank()) {
            aNotificationHandler.append(new Error("'paymentId' should not be empty"));
        }
        if (this.paymentMethodId == null || this.paymentMethodId.isBlank()) {
            aNotificationHandler.append(new Error("'paymentMethodId' should not be null or empty"));
        }
        if (this.installments <= 0) {
            aNotificationHandler.append(new Error("'installments' should be greater than zero"));
        }

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to create OrderPaymentDetails", aNotificationHandler);
        }
    }

    @Override
    public String toString() {
        return "OrderPaymentDetails(" +
                "paymentId='" + getPaymentId().orElse(null) + '\'' +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                ", installments=" + installments +
                ", tax=" + tax +
                ')';
    }
}
