package com.kaua.order.infrastructure.api.controllers;

import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.commands.CreateOrderItemCommand;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.infrastructure.api.OrderAPI;
import com.kaua.order.infrastructure.commands.CommandBus;
import com.kaua.order.infrastructure.models.OrderCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class OrderController implements OrderAPI {

    @Value("${kafka.consumers.orders-commands.topics[0]}")
    private String PLACE_ORDER_COMMAND_TOPIC;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final CommandBus commandBus;

    public OrderController(final CommandBus commandBus) {
        this.commandBus = Objects.requireNonNull(commandBus);
    }

    @Override
    public ResponseEntity<?> createOrder(OrderCreateRequest orderCreateRequest) {
        log.debug("Received a request to create an order: {}", orderCreateRequest);
        final var aCommandItems = orderCreateRequest.items()
                .stream().map(it -> CreateOrderItemCommand.with(it.sku(), it.quantity()))
                .collect(Collectors.toSet());

        final var aCommand = CreateOrderCommand.with(
                orderCreateRequest.customerId(),
                aCommandItems,
                orderCreateRequest.couponCode(),
                orderCreateRequest.paymentMethodId(),
                orderCreateRequest.installments(),
                orderCreateRequest.shippingCompany(),
                orderCreateRequest.shippingType(),
                orderCreateRequest.customerId(),
                IdUtils.generateIdWithoutHyphen() // in future use x-idempotency-key or x-request-id
        );

        this.commandBus.dispatch(aCommand, PLACE_ORDER_COMMAND_TOPIC);

        log.info("Order command dispatched: {}", aCommand);

        // and check to return body or no body
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
