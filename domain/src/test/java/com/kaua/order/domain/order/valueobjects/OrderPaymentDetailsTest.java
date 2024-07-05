package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderPaymentDetailsTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_thenReturnOrderPaymentDetails() {
        final var aPaymentId = "paymentId";
        final var aPaymentMethodId = "paymentMethodId";
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                aPaymentId,
                aPaymentMethodId,
                aInstallments,
                aTax
        );

        Assertions.assertNotNull(aOrderPaymentDetails);
        Assertions.assertEquals(aPaymentId, aOrderPaymentDetails.getPaymentId().get());
        Assertions.assertEquals(aPaymentMethodId, aOrderPaymentDetails.getPaymentMethodId());
        Assertions.assertEquals(aInstallments, aOrderPaymentDetails.getInstallments());
        Assertions.assertEquals(aTax, aOrderPaymentDetails.getTax());
    }

    @Test
    void givenAValidValuesWithoutPaymentId_whenCallCreate_thenReturnOrderPaymentDetails() {
        final var aPaymentMethodId = "paymentMethodId";
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                aPaymentMethodId,
                aInstallments,
                aTax
        );

        Assertions.assertNotNull(aOrderPaymentDetails);
        Assertions.assertTrue(aOrderPaymentDetails.getPaymentId().isEmpty());
        Assertions.assertEquals(aPaymentMethodId, aOrderPaymentDetails.getPaymentMethodId());
        Assertions.assertEquals(aInstallments, aOrderPaymentDetails.getInstallments());
        Assertions.assertEquals(aTax, aOrderPaymentDetails.getTax());
    }

    @Test
    void givenAnInvalidEmptyPaymentId_whenCallCreate_thenThrowNotificationException() {
        final String aPaymentId = "";
        final var aPaymentMethodId = "paymentMethodId";
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var expectedErrorMessage = "'paymentId' should not be empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderPaymentDetails.create(
                    aPaymentId,
                    aPaymentMethodId,
                    aInstallments,
                    aTax
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullPaymentMethodId_whenCallCreate_thenThrowNotificationException() {
        final var aPaymentId = "paymentId";
        final String aPaymentMethodId = null;
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var expectedErrorMessage = "'paymentMethodId' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderPaymentDetails.create(
                    aPaymentId,
                    aPaymentMethodId,
                    aInstallments,
                    aTax
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyPaymentMethodId_whenCallCreate_thenThrowNotificationException() {
        final var aPaymentId = "paymentId";
        final String aPaymentMethodId = "";
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var expectedErrorMessage = "'paymentMethodId' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderPaymentDetails.create(
                    aPaymentId,
                    aPaymentMethodId,
                    aInstallments,
                    aTax
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNegativeInstallments_whenCallCreate_thenThrowNotificationException() {
        final var aPaymentId = "paymentId";
        final var aPaymentMethodId = "paymentMethodId";
        final var aInstallments = -1;
        final var aTax = new BigDecimal("10.00");

        final var expectedErrorMessage = "'installments' should be greater than zero";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderPaymentDetails.create(
                    aPaymentId,
                    aPaymentMethodId,
                    aInstallments,
                    aTax
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void testCallToStringInOrderPaymentDetails() {
        final var aPaymentId = "paymentId";
        final var aPaymentMethodId = "paymentMethodId";
        final var aInstallments = 1;
        final var aTax = new BigDecimal("10.00");

        final var aOrderPaymentDetails = OrderPaymentDetails.create(
                aPaymentId,
                aPaymentMethodId,
                aInstallments,
                aTax
        );

        Assertions.assertNotNull(aOrderPaymentDetails.toString());
    }
}
