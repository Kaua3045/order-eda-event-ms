package com.kaua.order.domain.order;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.order.events.OrderCreationInitiatedEvent;
import com.kaua.order.domain.order.valueobjects.OrderAddress;
import com.kaua.order.domain.order.valueobjects.OrderCoupon;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

public class OrderValidationTest extends UnitTest {

    @Test
    void givenAnInvalidNullStatusInEvent_whenCallReconstruct_thenThrowsDomainException() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final String aOrderStatus = null;
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
                aOrderStatus,
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

        final var aExpectedErrorMessage = "'status' should not be null";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullCustomerIdInEvent_whenCallReconstruct_thenThrowsDomainException() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED.name();
        final String aCustomerId = null;
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
                aOrderStatus,
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

        final var aExpectedErrorMessage = "'customerId' should not be null or empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyCustomerIdInEvent_whenCallReconstruct_thenThrowsDomainException() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED.name();
        final String aCustomerId = "";
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
                aOrderStatus,
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

        final var aExpectedErrorMessage = "'customerId' should not be null or empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullItems_whenCallNewOrder_thenThrowsDomainException() {
        final var aCustomerId = "123";
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

        final var aExpectedErrorMessage = "'items' should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.newOrder(
                        aCustomerId,
                        null,
                        aOrderAddress,
                        aOrderCoupon,
                        aOrderPaymentDetails,
                        aOrderShippingDetails,
                        aTraceId
                ));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyItemsInEvent_whenCallReconstruct_thenThrowsDomainException() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED.name();
        final var aCustomerId = "123";
        final Set<OrderItem> aOrderItems = Set.of();
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
                aOrderStatus,
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

        final var aExpectedErrorMessage = "'items' should not be empty";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidDuplicateItemsInEvent_whenCallReconstruct_thenThrowsDomainException() {
        final var aOrderId = IdUtils.generateIdWithoutHyphen();
        final var aOrderVersion = 0;
        final var aOrderStatus = OrderStatus.CREATION_INITIATED.name();
        final var aCustomerId = "123";
        final Set<OrderItem> aOrderItems = Set.of(
                OrderItem.create("sku", 2, BigDecimal.TEN),
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
                aOrderStatus,
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

        final var aExpectedErrorMessage = "'items' should not have duplicated items";

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> Order.reconstruct(aEvents));

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }
}