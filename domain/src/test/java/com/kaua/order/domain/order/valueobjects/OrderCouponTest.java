package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderCouponTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_thenReturnOrderCoupon() {
        final var aCode = "couponCode";
        final var aPercentage = 10.0f;

        final var aOrderCoupon = OrderCoupon.create(
                aCode,
                aPercentage
        );

        Assertions.assertNotNull(aOrderCoupon);
        Assertions.assertEquals(aCode, aOrderCoupon.getCode());
        Assertions.assertEquals(aPercentage, aOrderCoupon.getPercentage());
    }

    @Test
    void givenAnInvalidEmptyCode_whenCallCreate_thenThrowNotificationException() {
        final String aCode = "";
        final var aPercentage = 10.0f;

        final var expectedErrorMessage = "'code' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderCoupon.create(
                    aCode,
                    aPercentage
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullCode_whenCallCreate_thenThrowNotificationException() {
        final String aCode = null;
        final var aPercentage = 10.0f;

        final var expectedErrorMessage = "'code' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderCoupon.create(
                    aCode,
                    aPercentage
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNegativePercentage_whenCallCreate_thenThrowNotificationException() {
        final var aCode = "couponCode";
        final var aPercentage = -10.0f;

        final var expectedErrorMessage = "'percentage' should be greater than zero";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderCoupon.create(
                    aCode,
                    aPercentage
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void testCallToStringInOrderCoupon() {
        final var aCode = "couponCode";
        final var aPercentage = 10.0f;

        final var aOrderCoupon = OrderCoupon.create(
                aCode,
                aPercentage
        );

        Assertions.assertNotNull(aOrderCoupon.toString());
    }
}
