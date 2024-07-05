package com.kaua.order.application.handlers.commands.validations;

import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.ValidationHandler;
import com.kaua.order.domain.validation.Validator;

public class CreateOrderCommandValidation extends Validator {

    private final CreateOrderCommand createOrderCommand;

    public CreateOrderCommandValidation(final CreateOrderCommand aCreateOrderCommand, final ValidationHandler handler) {
        super(handler);
        this.createOrderCommand = aCreateOrderCommand;
    }

    @Override
    public void validate() {
        validateCustomerId();
        validateItems();
        validateCouponCode();
        validatePaymentMethodId();
        validateInstallments();
        validateShippingCompany();
        validateShippingType();
    }

    private void validateCustomerId() {
        if (this.createOrderCommand.customerId() == null || this.createOrderCommand.customerId().isBlank()) {
            this.validationHandler().append(new Error("'customerId' should not be null or empty"));
        }
    }

    private void validateItems() {
        if (this.createOrderCommand.items().isEmpty()) {
            this.validationHandler().append(new Error("'items' should not be empty"));
            return;
        }

        final var aExistsDuplicatedItems = this.createOrderCommand.items().stream()
                .anyMatch(item -> this.createOrderCommand.items().stream()
                        .filter(i -> i.sku().equalsIgnoreCase(item.sku()))
                        .count() > 1);
        if (aExistsDuplicatedItems) {
            this.validationHandler().append(new Error("'items' should not have duplicated items"));
        }
    }

    private void validateCouponCode() {
        if (this.createOrderCommand.getCouponCode().isPresent() && this.createOrderCommand.getCouponCode().get().isBlank()) {
            this.validationHandler().append(new Error("'couponCode' should not be empty"));
        }
    }

    private void validatePaymentMethodId() {
        if (this.createOrderCommand.paymentMethodId() == null || this.createOrderCommand.paymentMethodId().isBlank()) {
            this.validationHandler().append(new Error("'paymentMethodId' should not be null or empty"));
        }
    }

    private void validateInstallments() {
        if (this.createOrderCommand.installments() <= 0) {
            this.validationHandler().append(new Error("'installments' should be greater than 0"));
        }
    }

    private void validateShippingCompany() {
        if (this.createOrderCommand.shippingCompany() == null || this.createOrderCommand.shippingCompany().isBlank()) {
            this.validationHandler().append(new Error("'shippingCompany' should not be null or empty"));
        }
    }

    private void validateShippingType() {
        if (this.createOrderCommand.shippingType() == null || this.createOrderCommand.shippingType().isBlank()) {
            this.validationHandler().append(new Error("'shippingType' should not be null or empty"));
        }
    }
}
