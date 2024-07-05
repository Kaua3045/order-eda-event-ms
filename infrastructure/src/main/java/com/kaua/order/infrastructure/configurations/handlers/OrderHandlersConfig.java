package com.kaua.order.infrastructure.configurations.handlers;

import com.kaua.order.application.gateways.CouponGateway;
import com.kaua.order.application.gateways.CustomerGateway;
import com.kaua.order.application.gateways.ProductGateway;
import com.kaua.order.application.handlers.create.AsyncCreateOrderHandler;
import com.kaua.order.application.repositories.EventStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class OrderHandlersConfig {

    private final CouponGateway couponGateway;
    private final ProductGateway productGateway;
    private final CustomerGateway customerGateway;
    private final EventStore eventStore;

    public OrderHandlersConfig(
            final CouponGateway couponGateway,
            final ProductGateway productGateway,
            final CustomerGateway customerGateway,
            final EventStore eventStore
    ) {
        this.couponGateway = Objects.requireNonNull(couponGateway);
        this.productGateway = Objects.requireNonNull(productGateway);
        this.customerGateway = Objects.requireNonNull(customerGateway);
        this.eventStore = Objects.requireNonNull(eventStore);
    }

    @Bean
    public AsyncCreateOrderHandler asyncCreateOrderHandler() {
        return new AsyncCreateOrderHandler(
                couponGateway,
                productGateway,
                customerGateway,
                eventStore
        );
    }
}
