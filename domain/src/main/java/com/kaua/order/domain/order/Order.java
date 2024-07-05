package com.kaua.order.domain.order;

import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.exceptions.UnknownEventReceivedException;
import com.kaua.order.domain.order.events.OrderCreationInitiatedEvent;
import com.kaua.order.domain.order.identifiers.OrderId;
import com.kaua.order.domain.order.validation.OrderValidation;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderCoupon;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.ValidationHandler;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

public class Order extends AggregateRoot<OrderId> {

    private OrderStatus status;
    private String customerId;
    private Set<OrderItem> items;
    private OrderAddress shippingAddress;
    private BigDecimal totalAmount;
    private OrderCoupon coupon;
    private OrderPaymentDetails paymentDetails;
    private OrderShippingDetails shippingDetails;
    private Instant deliveredAt;

    private Order(final OrderId aOrderId, final long aVersion) {
        super(aOrderId, aVersion);
        this.items = new HashSet<>();
    }

    private Order(
            final OrderId aOrderId,
            final long aVersion,
            final OrderStatus aStatus,
            final String aCustomerId,
            final Set<OrderItem> aItems,
            final OrderAddress aShippingAddress,
            final BigDecimal aTotalAmount,
            final OrderCoupon aCoupon,
            final OrderPaymentDetails aPaymentDetails,
            final OrderShippingDetails aShippingDetails,
            final Instant aDeliveredAt
    ) {
        super(aOrderId, aVersion);
        this.status = aStatus;
        this.customerId = aCustomerId;
        this.items = aItems == null ? new HashSet<>() : aItems;
        this.shippingAddress = aShippingAddress;
        this.totalAmount = aTotalAmount;
        this.coupon = aCoupon;
        this.paymentDetails = aPaymentDetails;
        this.shippingDetails = aShippingDetails;
        this.deliveredAt = aDeliveredAt;
        selfValidate();
    }

    public static Order newOrder(
            final String aCustomerId,
            final Set<OrderItem> aItems,
            final OrderAddress aShippingAddress,
            final OrderCoupon aCoupon,
            final OrderPaymentDetails aPaymentDetails,
            final OrderShippingDetails aShippingDetails,
            final String aTraceId
    ) {
        final var aOrderId = OrderId.generate();

        final var aOrder = new Order(
                aOrderId,
                0,
                OrderStatus.CREATION_INITIATED,
                aCustomerId,
                aItems,
                aShippingAddress,
                BigDecimal.ZERO,
                aCoupon,
                aPaymentDetails,
                aShippingDetails,
                null
        );
        aOrder.calculateTotalAmountWithoutTax();
        aOrder.registerEvent(OrderCreationInitiatedEvent.from(
                aOrder.getId().getValue(),
                aOrder.getStatus().name(),
                aOrder.getCustomerId(),
                aOrder.getTotalAmount(),
                aOrder.getItems(),
                aOrder.getShippingAddress(),
                aOrder.getCoupon().orElse(null),
                aOrder.getPaymentDetails(),
                aOrder.getShippingDetails(),
                aOrder.getVersion(),
                aOrder.getCustomerId(),
                aTraceId
        ));

        return aOrder;
    }

    public static Order reconstruct(final List<DomainEvent> aEvents) {
        if (aEvents == null || aEvents.isEmpty()) {
            throw DomainException.with(new Error("cannot reconstruct order without events"));
        }

        aEvents.sort(Comparator.comparing(DomainEvent::aggregateVersion));

        final var aOrder = new Order(OrderId.from(aEvents.get(0).aggregateId()), aEvents.get(0).aggregateVersion());
        aEvents.forEach(aOrder::apply);
        return aOrder;
    }

    // talvez esse nem exista, ele deve trigar o shipping calculation command
    // o shipping calculation command retorna o shipping calculated event
    // que triga o payment tax calculation command
    // que retorna o payment tax calculated event
    // que triga o create order command
    // que retorna o order placed event (esse aqui já pode enviar email dai)
    // que triga o stock reservation command
    // que retorna o stock confirmed event
    // que triga o payment initiation command
    // que retorna o payment initiated event
    // que triga o payment approved command
    // que retorna o payment approved event
    // que triga o preparing for shipping command
    // que retorna o preparing for shipping event
    // que triga o shipping command
    // que retorna o shipped event
    public void on(final OrderCreationInitiatedEvent aEvent) {
        this.setVersion(aEvent.aggregateVersion());
        this.status = OrderStatus.of(aEvent.orderStatus()).orElse(null);
        this.customerId = aEvent.who();
        this.items = aEvent.items();
        this.shippingAddress = aEvent.shippingAddress();
        this.totalAmount = aEvent.totalAmount();
        this.coupon = aEvent.coupon();
        this.paymentDetails = aEvent.paymentDetails();
        this.shippingDetails = aEvent.shippingDetails();
        selfValidate();
    }

    private void apply(final DomainEvent aEvent) {
        switch (aEvent.eventType()) {
            case OrderCreationInitiatedEvent.EVENT_TYPE -> on((OrderCreationInitiatedEvent) aEvent);
            default -> throw new UnknownEventReceivedException(aEvent.eventType());
        }
    }

    private void calculateTotalAmountWithoutTax() {
        this.totalAmount = this.items.stream()
                .map(OrderItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (getCoupon().isPresent()) {
            this.totalAmount = this.totalAmount.subtract(this.totalAmount
                    .multiply(BigDecimal.valueOf(this.coupon.getPercentage() / 100)));
        }

        this.totalAmount = this.totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public void validate(ValidationHandler aHandler) {
        new OrderValidation(this, aHandler).validate();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Set<OrderItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public OrderAddress getShippingAddress() {
        return shippingAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Optional<OrderCoupon> getCoupon() {
        return Optional.ofNullable(coupon);
    }

    public OrderPaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public OrderShippingDetails getShippingDetails() {
        return shippingDetails;
    }

    public Optional<Instant> getDeliveredAt() {
        return Optional.ofNullable(deliveredAt);
    }

    private void selfValidate() {
        final var aNotificationHandler = NotificationHandler.create();
        validate(aNotificationHandler);

        if (aNotificationHandler.hasError()) {
            throw DomainException.with(aNotificationHandler.getErrors());
        }
    }

    @Override
    public String toString() {
        return "Order(" +
                "id=" + getId().getValue() +
                ", status='" + status.name() + '\'' +
                ", customerId='" + customerId + '\'' +
                ", items=" + items.size() +
                ", shippingAddress=" + shippingAddress.toString() +
                ", totalAmount=" + totalAmount +
                ", coupon=" + coupon.toString() +
                ", paymentDetails=" + paymentDetails.toString() +
                ", shippingDetails=" + shippingDetails.toString() +
                ", deliveredAt=" + deliveredAt +
                ')';
    }
}
