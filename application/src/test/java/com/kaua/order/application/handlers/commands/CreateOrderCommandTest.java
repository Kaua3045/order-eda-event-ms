package com.kaua.order.application.handlers.commands;

import com.kaua.order.application.CommandTest;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class CreateOrderCommandTest extends CommandTest {

    @Test
    void givenAnInvalidNullCustomerId_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = null;
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'customerId' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyCustomerId_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = "";
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'customerId' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyItems_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'items' should not be empty";

        final var aCommandItems = new HashSet<CreateOrderItemCommand>();

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        aCommandItems,
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidDuplicatedItems_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'items' should not have duplicated items";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                9999
        );

        final var aCommandItemsDuplicated = CreateOrderItemCommand.with(
                aItemSku,
                990
        );
        final var aCommandItemsUnique = CreateOrderItemCommand.with(
                Fixture.itemSku(),
                555
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems, aCommandItemsDuplicated, aCommandItemsUnique),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyCouponCode_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = "";
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'couponCode' should not be empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullPaymentMethodId_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final String aPaymentMethodId = null;
        final var aInstallments = 1;

        final var expectedErrorMessage = "'paymentMethodId' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyPaymentMethodId_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final String aPaymentMethodId = "";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'paymentMethodId' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidZeroInstallments_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final int aInstallments = 0;

        final var expectedErrorMessage = "'installments' should be greater than 0";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullShippingCompany_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final String aShippingCompany = null;
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'shippingCompany' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyShippingCompany_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final String aShippingCompany = "";
        final var aShippingType = Fixture.shippingType();
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'shippingCompany' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullShippingType_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final String aShippingType = null;
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'shippingType' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyShippingType_whenCreateOrderCommand_thenThrowsException() {
        final var aTraceId = IdUtils.generateIdWithoutHyphen();

        final String aCustomerId = Fixture.customerId();
        final var aItemSku = Fixture.itemSku();
        final var aItemQuantity = Fixture.itemQuantity();
        final var aShippingCompany = Fixture.shippingCompany();
        final String aShippingType = "";
        final var aCouponCode = Fixture.couponCode();
        final var aPaymentMethodId = "1";
        final var aInstallments = 1;

        final var expectedErrorMessage = "'shippingType' should not be null or empty";

        final var aCommandItems = CreateOrderItemCommand.with(
                aItemSku,
                aItemQuantity
        );

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> CreateOrderCommand.with(
                        aCustomerId,
                        Set.of(aCommandItems),
                        aCouponCode,
                        aPaymentMethodId,
                        aInstallments,
                        aShippingCompany,
                        aShippingType,
                        aCustomerId,
                        aTraceId
                ));

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
