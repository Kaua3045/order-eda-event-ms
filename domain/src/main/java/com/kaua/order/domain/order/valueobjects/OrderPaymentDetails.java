package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;
import java.util.Optional;

public record OrderPaymentDetails(
        String paymentId,
        String paymentMethodId,
        int installments,
        BigDecimal tax
) {

    public static OrderPaymentDetails create(
            final String aPaymentId,
            final String aPaymentMethodId,
            final int aInstallments,
            final BigDecimal aTax
    ) {
        final var aDetails = new OrderPaymentDetails(aPaymentId, aPaymentMethodId, aInstallments, aTax);
        aDetails.validate();
        return aDetails;
    }

    public static OrderPaymentDetails create(
            final String aPaymentMethodId,
            final int aInstallments,
            final BigDecimal aTax
    ) {
        final var aDetails = new OrderPaymentDetails(null, aPaymentMethodId, aInstallments, aTax);
        aDetails.validate();
        return aDetails;
    }

    public static OrderPaymentDetails create(
            final String aPaymentMethodId,
            final int aInstallments
    ) {
        final var aDetails = new OrderPaymentDetails(null, aPaymentMethodId, aInstallments, BigDecimal.ZERO);
        aDetails.validate();
        return aDetails;
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
