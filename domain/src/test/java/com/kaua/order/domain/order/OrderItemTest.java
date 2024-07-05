package com.kaua.order.domain.order;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderItemTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_thenOrderItemIsCreated() {
        final var aSku = "sku";
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;

        final var aOrderItem = OrderItem.create(aSku, aQuantity, aPrice);

        Assertions.assertNotNull(aOrderItem);
        Assertions.assertNotNull(aOrderItem.getOrderItemId());
        Assertions.assertEquals(aSku, aOrderItem.getSku());
        Assertions.assertEquals(aQuantity, aOrderItem.getQuantity());
        Assertions.assertEquals(aPrice, aOrderItem.getUnitAmount());
        Assertions.assertEquals(BigDecimal.valueOf(20), aOrderItem.getTotalAmount());
    }

    @Test
    void givenAValidValues_whenCallWithOrderItem_thenOrderItemIsCreated() {
        final var aOrderItemId = IdUtils.generateIdWithoutHyphen();
        final var aSku = "sku";
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;
        final var aTotal = BigDecimal.valueOf(20);

        final var aOrderItem = OrderItem.with(
                aOrderItemId,
                aSku,
                aQuantity,
                aPrice,
                aTotal
        );

        Assertions.assertNotNull(aOrderItem);
        Assertions.assertEquals(aOrderItemId, aOrderItem.getOrderItemId());
        Assertions.assertEquals(aSku, aOrderItem.getSku());
        Assertions.assertEquals(aQuantity, aOrderItem.getQuantity());
        Assertions.assertEquals(aPrice, aOrderItem.getUnitAmount());
        Assertions.assertEquals(aTotal, aOrderItem.getTotalAmount());
    }

    @Test
    void givenAnInvalidNullOrderItemId_whenCallWith_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;
        final var aTotal = BigDecimal.valueOf(20);

        final var expectedErrorMessage = "'orderItemId' should not be null or empty";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.with(
                        null,
                        aSku,
                        aQuantity,
                        aPrice,
                        aTotal
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankOrderItemId_whenCallWith_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;
        final var aTotal = BigDecimal.valueOf(20);

        final var expectedErrorMessage = "'orderItemId' should not be null or empty";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.with(
                        "",
                        aSku,
                        aQuantity,
                        aPrice,
                        aTotal
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullSku_whenCallCreate_thenThrowNotificationException() {
        final var aOrderItemId = IdUtils.generateIdWithoutHyphen();
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;
        final var aTotal = BigDecimal.valueOf(20);

        final var expectedErrorMessage = "'sku' should not be null or empty";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.with(
                        aOrderItemId,
                        null,
                        aQuantity,
                        aPrice,
                        aTotal
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankSku_whenCallCreate_thenThrowNotificationException() {
        final var aQuantity = 2;
        final var aPrice = BigDecimal.TEN;

        final var expectedErrorMessage = "'sku' should not be null or empty";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.create(
                        "",
                        aQuantity,
                        aPrice
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNegativeQuantity_whenCallCreate_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aPrice = BigDecimal.TEN;

        final var expectedErrorMessage = "'quantity' should be greater than 0";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.create(
                        aSku,
                        -1,
                        aPrice
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidZeroQuantity_whenCallCreate_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aPrice = BigDecimal.TEN;

        final var expectedErrorMessage = "'quantity' should be greater than 0";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.create(
                        aSku,
                        0,
                        aPrice
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullUnitAmount_whenCallWith_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aQuantity = 2;

        final var expectedErrorMessage = "'unitAmount' should be greater than 0";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.with(
                        "1",
                        aSku,
                        aQuantity,
                        null,
                        BigDecimal.valueOf(20)
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidZeroUnitAmount_whenCallCreate_thenThrowNotificationException() {
        final var aSku = "sku";
        final var aQuantity = 2;

        final var expectedErrorMessage = "'unitAmount' should be greater than 0";

        final var aException = Assertions.assertThrows(
                NotificationException.class,
                () -> OrderItem.create(
                        aSku,
                        aQuantity,
                        BigDecimal.valueOf(0)
                )
        );

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }
}
