package com.kaua.order.application.handlers.create;

import com.kaua.order.application.CommandHandler;
import com.kaua.order.application.gateways.CouponGateway;
import com.kaua.order.application.gateways.CustomerGateway;
import com.kaua.order.application.gateways.ProductGateway;
import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.commands.CreateOrderItemCommand;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.order.Order;
import com.kaua.order.domain.order.OrderItem;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderCoupon;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.validation.Error;

import java.util.Objects;
import java.util.stream.Collectors;

public class AsyncCreateOrderHandler extends CommandHandler<CreateOrderCommand> {

    private final CouponGateway couponGateway;
    private final ProductGateway productGateway;
    private final CustomerGateway customerGateway;
    private final EventStore eventStore;

    public AsyncCreateOrderHandler(
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

    @Override
    public void handle(final CreateOrderCommand aCommand) {
        // tratar caso tenha coupon e caso não tenha coupon
        final var aCoupon = aCommand.getCouponCode().map(this.couponGateway::applyCoupon);
        final var aCustomer = this.customerGateway.getCustomerDetails(aCommand.customerId());

        final var aProductsSkus = aCommand.items().stream()
                .map(CreateOrderItemCommand::sku)
                .toList();
        final var aItems = this.productGateway.getProductsDetailsBySkus(aProductsSkus);

        if (aItems.isEmpty() || aItems.size() != aProductsSkus.size()) {
            throw DomainException.with(new Error("Product details not found"));
        }

        // todo: check to increment performance in this part
        final var aItemsQuantity = aCommand.items().stream()
                .collect(Collectors.toMap(CreateOrderItemCommand::sku, CreateOrderItemCommand::quantity));

        final var aOrderItems = aItems.stream()
                .map(it -> OrderItem.create(
                        it.sku(),
                        aItemsQuantity.get(it.sku()),
                        it.unitPrice()
                ))
                .collect(Collectors.toSet());

        final var aOrderCoupon = aCoupon.map(it -> OrderCoupon.create(it.couponCode(), it.percentage()))
                .orElse(null);
        final var aOrderAddress = OrderAddress.create(
                aCustomer.address().street(),
                aCustomer.address().number(),
                aCustomer.address().complement(),
                aCustomer.address().city(),
                aCustomer.address().state(),
                aCustomer.address().zipCode()
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                aCommand.shippingCompany(),
                aCommand.shippingType()
        );
        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                aCommand.paymentMethodId(),
                aCommand.installments()
        );

        final var aOrder = Order.newOrder(
                aCustomer.customerId(),
                aOrderItems,
                aOrderAddress,
                aOrderCoupon,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aCommand.traceId()
        );

        this.eventStore.save(aOrder);
    }
}
