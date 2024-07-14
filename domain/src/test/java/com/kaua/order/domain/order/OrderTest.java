package com.kaua.order.domain.order;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.exceptions.UnknownEventReceivedException;
import com.kaua.order.domain.order.events.OrderCreationInitiatedEvent;
import com.kaua.order.domain.order.events.OrderPaymentTaxCalculatedEvent;
import com.kaua.order.domain.order.events.OrderShippingCostCalculatedEvent;
import com.kaua.order.domain.order.events.external.PaymentTaxCalculatedEvent;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;
import com.kaua.order.domain.order.identifiers.OrderId;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderCoupon;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;

public class OrderTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewOrder_thenOrderIsCreationInitiatedAndReturnWithEvent() {
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        // 10% de desconto
        final var aOrderCoupon = OrderCoupon.create("COUPON", 10f);
        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "123",
                "shippingCompany",
                "shippingType",
                new BigDecimal("2.00")
        );
        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrder = Order.newOrder(
                aCustomerId,
                aOrderItems,
                aOrderAddress,
                aOrderCoupon,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aTraceId
        );

        Assertions.assertNotNull(aOrder);
        Assertions.assertEquals(OrderStatus.CREATION_INITIATED, aOrder.getStatus());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aOrderItems, aOrder.getItems());
        Assertions.assertEquals(aOrderAddress, aOrder.getShippingAddress());
        Assertions.assertEquals(new BigDecimal("18.00"), aOrder.getTotalAmount());
        Assertions.assertEquals(aOrderCoupon.getCode(), aOrder.getCoupon().get().getCode());
        Assertions.assertEquals(aOrderCoupon.getPercentage(), aOrder.getCoupon().get().getPercentage());
        Assertions.assertEquals(aOrderPaymentDetails, aOrder.getPaymentDetails());
        Assertions.assertEquals(aOrderShippingDetails, aOrder.getShippingDetails());
        Assertions.assertTrue(aOrder.getDeliveredAt().isEmpty());
        Assertions.assertEquals(1, aOrder.getDomainEvents().size());
    }

    @Test
    void givenAValidValuesWithoutCoupon_whenCallNewOrder_thenOrderIsCreationInitiatedAndReturnWithEvent() {
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "123",
                "shippingCompany",
                "shippingType",
                new BigDecimal("2.00")
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrder = Order.newOrder(
                aCustomerId,
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aTraceId
        );

        Assertions.assertNotNull(aOrder);
        Assertions.assertEquals(OrderStatus.CREATION_INITIATED, aOrder.getStatus());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aOrderItems, aOrder.getItems());
        Assertions.assertEquals(aOrderAddress, aOrder.getShippingAddress());
        Assertions.assertEquals(new BigDecimal("20.00"), aOrder.getTotalAmount());
        Assertions.assertTrue(aOrder.getCoupon().isEmpty());
        Assertions.assertEquals(aOrderPaymentDetails, aOrder.getPaymentDetails());
        Assertions.assertEquals(aOrderShippingDetails, aOrder.getShippingDetails());
        Assertions.assertTrue(aOrder.getDeliveredAt().isEmpty());
        Assertions.assertEquals(1, aOrder.getDomainEvents().size());
    }

    @Test
    void givenAValidEvents_whenCallReconstruct_thenOrderIsReconstructed() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED;
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "123",
                "shippingCompany",
                "shippingType",
                new BigDecimal("2.00")
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrderCreationInitiatedEvent = OrderCreationInitiatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aCustomerId,
                new BigDecimal("20.00"),
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreationInitiatedEvent);

        final var aOrder = Order.reconstruct(aEvents);

        Assertions.assertNotNull(aOrder);
        Assertions.assertEquals(aOrderId, aOrder.getId().getValue());
        Assertions.assertEquals(aOrderVersion, aOrder.getVersion());
        Assertions.assertEquals(aOrderStatus, aOrder.getStatus());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aOrderItems, aOrder.getItems());
        Assertions.assertEquals(aOrderAddress, aOrder.getShippingAddress());
        Assertions.assertEquals(new BigDecimal("20.00"), aOrder.getTotalAmount());
        Assertions.assertTrue(aOrder.getCoupon().isEmpty());
        Assertions.assertEquals(aOrderPaymentDetails, aOrder.getPaymentDetails());
        Assertions.assertEquals(aOrderShippingDetails, aOrder.getShippingDetails());
    }

    @Test
    void testOrderIdEqualsAndHashCode() {
        final var aOrderId = OrderId.from("123456789");
        final var anotherOrderId = OrderId.from("123456789");

        Assertions.assertTrue(aOrderId.equals(anotherOrderId));
        Assertions.assertTrue(aOrderId.equals(aOrderId));
        Assertions.assertFalse(aOrderId.equals(null));
        Assertions.assertFalse(aOrderId.equals(""));
        Assertions.assertEquals(aOrderId.hashCode(), anotherOrderId.hashCode());
    }

    @Test
    void givenAnNotExpectedEvent_whenCallReconstruct_thenThrowUnknownEventReceivedException() {
        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(new SampleEntityEvent(IdUtils.generateIdWithoutHyphen()));

        final var aExpectedErrorMessage = "unknown event %s received and cannot be processed"
                .formatted("SampleEntityEvent");

        final var aException = Assertions.assertThrows(UnknownEventReceivedException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getMessage());
    }

    @Test
    void givenAnNullEventsList_whenCallReconstruct_thenThrowsException() {
        final var aExpectedErrorMessage = "cannot reconstruct order without events";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(null));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnEmptyEventsList_whenCallReconstruct_thenThrowsException() {
        final var aExpectedErrorMessage = "cannot reconstruct order without events";

        final var aEvents = new ArrayList<DomainEvent>();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void testCallToStringInOrderAggregate() {
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        // 10% de desconto
        final var aOrderCoupon = OrderCoupon.create("COUPON", 10f);
        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "123",
                "shippingCompany",
                "shippingType",
                new BigDecimal("2.00")
        );
        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrder = Order.newOrder(
                aCustomerId,
                aOrderItems,
                aOrderAddress,
                aOrderCoupon,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aTraceId
        );

        Assertions.assertNotNull(aOrder.toString());
    }

    @Test
    void givenAValidShippingCostCalculatedEvent_whenCallHandle_thenHandleEventAndRegisterInternalEvent() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED;
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "shippingCompany",
                "shippingType"
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrderCreationInitiatedEvent = OrderCreationInitiatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aCustomerId,
                new BigDecimal("20.00"),
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreationInitiatedEvent);

        final var aOrder = Order.reconstruct(aEvents);

        final var aShippingCostCalculatedEvent = ShippingCostCalculatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aOrder.getTotalAmount(),
                aOrder.getShippingAddress(),
                OrderShippingDetails.create(
                        aOrderShippingDetails.getShippingCompany(),
                        aOrderShippingDetails.getShippingType(),
                        new BigDecimal("2.00")
                ),
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        aOrder.handle(aShippingCostCalculatedEvent);

        Assertions.assertEquals(new BigDecimal("22.00"), aOrder.getTotalAmount());
        Assertions.assertEquals(1, aOrder.getDomainEvents().size());
        Assertions.assertInstanceOf(OrderShippingCostCalculatedEvent.class, aOrder.getDomainEvents().get(0));
        Assertions.assertEquals(1, aOrder.getVersion());
    }

    @Test
    void givenAValidOrderShippingCostCalculatedEvent_whenCallReconstruct_thenOrderIsReconstructed() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED;
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "shippingCompany",
                "shippingType"
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrderCreationInitiatedEvent = OrderCreationInitiatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aCustomerId,
                new BigDecimal("20.00"),
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        final var aShippingCostCalculatedEvent = OrderShippingCostCalculatedEvent.from(
                aOrderId,
                OrderStatus.SHIPPING_CALCULATED.name(),
                new BigDecimal("22.00"),
                aOrderAddress,
                aOrderPaymentDetails,
                OrderShippingDetails.create(
                        aOrderShippingDetails.getShippingCompany(),
                        aOrderShippingDetails.getShippingType(),
                        new BigDecimal("2.00")
                ),
                1,
                aCustomerId,
                aTraceId
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreationInitiatedEvent);
        aEvents.add(aShippingCostCalculatedEvent);

        final var aOrder = Order.reconstruct(aEvents);

        Assertions.assertNotNull(aOrder);
        Assertions.assertEquals(aOrderId, aOrder.getId().getValue());
        Assertions.assertEquals(1, aOrder.getVersion());
        Assertions.assertEquals(OrderStatus.SHIPPING_CALCULATED, aOrder.getStatus());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aOrderItems, aOrder.getItems());
        Assertions.assertEquals(aOrderAddress, aOrder.getShippingAddress());
        Assertions.assertEquals(new BigDecimal("22.00"), aOrder.getTotalAmount());
    }

    @Test
    void givenAValidPaymentTaxCalculatedEvent_whenCallHandle_thenApplyModificationsAndRegisterOrderPaymentTaxCalculatedEvent() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED;
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "shippingCompany",
                "shippingType"
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrderCreationInitiatedEvent = OrderCreationInitiatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aCustomerId,
                new BigDecimal("20.00"),
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        final var aShippingCostCalculatedEvent = OrderShippingCostCalculatedEvent.from(
                aOrderId,
                OrderStatus.SHIPPING_CALCULATED.name(),
                new BigDecimal("22.00"),
                aOrderAddress,
                aOrderPaymentDetails,
                OrderShippingDetails.create(
                        aOrderShippingDetails.getShippingCompany(),
                        aOrderShippingDetails.getShippingType(),
                        new BigDecimal("2.00")
                ),
                1,
                aCustomerId,
                aTraceId
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreationInitiatedEvent);
        aEvents.add(aShippingCostCalculatedEvent);

        final var aOrder = Order.reconstruct(aEvents);

        final var aPaymentTaxCalculatedEvent = PaymentTaxCalculatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                new BigDecimal("22.00"),
                OrderPaymentDetails.create(
                        aOrder.getPaymentDetails().paymentMethodId(),
                        aOrder.getPaymentDetails().installments(),
                        new BigDecimal("5.00")
                ),
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        aOrder.handle(aPaymentTaxCalculatedEvent);

        Assertions.assertEquals(new BigDecimal("27.00"), aOrder.getTotalAmount());
        Assertions.assertEquals(1, aOrder.getDomainEvents().size());
        Assertions.assertInstanceOf(OrderPaymentTaxCalculatedEvent.class, aOrder.getDomainEvents().get(0));
    }

    @Test
    void givenAValidOrderPaymentTaxCalculatedEvent_whenCallReconstruct_thenOrderIsReconstructed() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED;
        final var aCustomerId = "123";
        final var aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN)
        );
        final var aOrderAddress = OrderAddress.create(
                "street",
                "number",
                "complement",
                "city",
                "state",
                "zipCode"
        );

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                "paymentMethodId",
                1
        );
        final var aOrderShippingDetails = OrderShippingDetails.create(
                "shippingCompany",
                "shippingType"
        );

        final var aTraceId = IdUtils.generateIdWithHyphen();

        final var aOrderCreationInitiatedEvent = OrderCreationInitiatedEvent.from(
                aOrderId,
                aOrderStatus.name(),
                aCustomerId,
                new BigDecimal("20.00"),
                aOrderItems,
                aOrderAddress,
                null,
                aOrderPaymentDetails,
                aOrderShippingDetails,
                aOrderVersion,
                aCustomerId,
                aTraceId
        );

        final var aShippingCostCalculatedEvent = OrderShippingCostCalculatedEvent.from(
                aOrderId,
                OrderStatus.SHIPPING_CALCULATED.name(),
                new BigDecimal("22.00"),
                aOrderAddress,
                aOrderPaymentDetails,
                OrderShippingDetails.create(
                        aOrderShippingDetails.getShippingCompany(),
                        aOrderShippingDetails.getShippingType(),
                        new BigDecimal("2.00")
                ),
                1,
                aCustomerId,
                aTraceId
        );

        final var aPaymentTaxCalculatedEvent = OrderPaymentTaxCalculatedEvent.from(
                aOrderId,
                OrderStatus.PAYMENT_TAX_CALCULATED.name(),
                new BigDecimal("27.00"),
                OrderPaymentDetails.create(
                        aOrderPaymentDetails.paymentMethodId(),
                        aOrderPaymentDetails.installments(),
                        new BigDecimal("5.00")
                ),
                2,
                aCustomerId,
                aTraceId
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreationInitiatedEvent);
        aEvents.add(aShippingCostCalculatedEvent);
        aEvents.add(aPaymentTaxCalculatedEvent);

        final var aOrder = Order.reconstruct(aEvents);

        Assertions.assertNotNull(aOrder);
        Assertions.assertEquals(aOrderId, aOrder.getId().getValue());
        Assertions.assertEquals(2, aOrder.getVersion());
        Assertions.assertEquals(OrderStatus.PAYMENT_TAX_CALCULATED, aOrder.getStatus());
        Assertions.assertEquals(aCustomerId, aOrder.getCustomerId());
        Assertions.assertEquals(aOrderItems, aOrder.getItems());
        Assertions.assertEquals(aOrderAddress, aOrder.getShippingAddress());
        Assertions.assertEquals(new BigDecimal("27.00"), aOrder.getTotalAmount());
        Assertions.assertEquals(aOrderPaymentDetails.paymentMethodId(), aOrder.getPaymentDetails().paymentMethodId());
        Assertions.assertEquals(aOrderPaymentDetails.installments(), aOrder.getPaymentDetails().installments());
        Assertions.assertEquals(new BigDecimal("5.00"), aOrder.getPaymentDetails().tax());
    }


    private record SampleEntityEvent(
            String aggregateId, String eventId, String eventType, String eventClassName, Instant occurredOn,
            long aggregateVersion, String who, String traceId) implements DomainEvent {

        public SampleEntityEvent(final String id) {
            this(id, IdUtils.generateIdWithoutHyphen(),
                    "SampleEntityEvent",
                    SampleEntityEvent.class.getName(),
                    InstantUtils.now(),
                    1,
                    "user teste",
                    IdUtils.generateIdWithoutHyphen());
        }
    }
}
