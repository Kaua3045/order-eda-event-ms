package com.kaua.order.domain.order.validation;

import com.kaua.order.domain.order.Order;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.ValidationHandler;
import com.kaua.order.domain.validation.Validator;

public class OrderValidation extends Validator {

    private final Order order;

    public OrderValidation(final Order aOrder, final ValidationHandler handler) {
        super(handler);
        this.order = aOrder;
    }

    @Override
    public void validate() {
        validateOrderStatus();
        validateCustomerId();
        validateItems();
    }

    private void validateOrderStatus() {
        if (this.order.getStatus() == null) {
            this.validationHandler().append(new Error("'status' should not be null"));
        }
    }

    private void validateCustomerId() {
        if (this.order.getCustomerId() == null || this.order.getCustomerId().isBlank()) {
            this.validationHandler().append(new Error("'customerId' should not be null or empty"));
        }
    }

    private void validateItems() {
        if (this.order.getItems().isEmpty()) {
            this.validationHandler().append(new Error("'items' should not be empty"));
            return;
        }

        final var aExistsDuplicatedItems = this.order.getItems().stream()
                .anyMatch(item -> this.order.getItems().stream()
                        .filter(i -> i.getSku().equalsIgnoreCase(item.getSku()))
                        .count() > 1);
        if (aExistsDuplicatedItems) {
            this.validationHandler().append(new Error("'items' should not have duplicated items"));
        }
    }
}
