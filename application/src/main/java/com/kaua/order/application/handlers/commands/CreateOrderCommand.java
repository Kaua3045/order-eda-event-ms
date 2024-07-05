package com.kaua.order.application.handlers.commands;

import com.kaua.order.application.handlers.commands.validations.CreateOrderCommandValidation;
import com.kaua.order.domain.commands.InternalCommand;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public record CreateOrderCommand(
        String customerId,
        Set<CreateOrderItemCommand> items,
        String couponCode,
        String paymentMethodId,
        int installments,
        String shippingCompany,
        String shippingType,
        String commandId,
        String commandType,
        Instant occurredOn,
        String who,
        String traceId
) implements InternalCommand {

    public static final String COMMAND_TYPE = "CreateOrderCommand";

    private CreateOrderCommand(
            final String customerId,
            final Set<CreateOrderItemCommand> items,
            final String couponCode,
            final String paymentMethodId,
            final int installments,
            final String shippingCompany,
            final String shippingType,
            final String who,
            final String traceId
    ) {
        this(
                customerId,
                items,
                couponCode,
                paymentMethodId,
                installments,
                shippingCompany,
                shippingType,
                IdUtils.generateIdWithoutHyphen(),
                COMMAND_TYPE,
                InstantUtils.now(),
                who,
                traceId
        );
        selfValidate();
    }

    public static CreateOrderCommand with(
            final String customerId,
            final Set<CreateOrderItemCommand> items,
            final String couponCode,
            final String paymentMethodId,
            final int installments,
            final String shippingCompany,
            final String shippingType,
            final String who,
            final String traceId
    ) {
        return new CreateOrderCommand(
                customerId,
                items,
                couponCode,
                paymentMethodId,
                installments,
                shippingCompany,
                shippingType,
                who,
                traceId
        );
    }

    public Optional<String> getCouponCode() {
        return Optional.ofNullable(couponCode);
    }

    private void selfValidate() {
        final var aNotificationHandler = NotificationHandler.create();
        new CreateOrderCommandValidation(this, aNotificationHandler).validate();

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to instantiate CreateOrderCommand", aNotificationHandler);
        }
    }
}
