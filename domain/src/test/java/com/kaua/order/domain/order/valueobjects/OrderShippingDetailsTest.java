package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class OrderShippingDetailsTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_thenReturnOrderShippingDetails() {
        final var aShippingId = "shippingId";
        final var aShippingCompany = "shippingCompany";
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var aOrderShippingDetails = OrderShippingDetails.create(
                aShippingId,
                aShippingCompany,
                aShippingType,
                aCost
        );

        Assertions.assertNotNull(aOrderShippingDetails);
        Assertions.assertEquals(aShippingId, aOrderShippingDetails.getShippingId().get());
        Assertions.assertEquals(aShippingCompany, aOrderShippingDetails.getShippingCompany());
        Assertions.assertEquals(aShippingType, aOrderShippingDetails.getShippingType());
        Assertions.assertEquals(aCost, aOrderShippingDetails.getCost());
    }

    @Test
    void givenAValidValuesWithoutShippingId_whenCallCreate_thenReturnOrderShippingDetails() {
        final var aShippingCompany = "shippingCompany";
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var aOrderShippingDetails = OrderShippingDetails.create(
                aShippingCompany,
                aShippingType,
                aCost
        );

        Assertions.assertNotNull(aOrderShippingDetails);
        Assertions.assertTrue(aOrderShippingDetails.getShippingId().isEmpty());
        Assertions.assertEquals(aShippingCompany, aOrderShippingDetails.getShippingCompany());
        Assertions.assertEquals(aShippingType, aOrderShippingDetails.getShippingType());
        Assertions.assertEquals(aCost, aOrderShippingDetails.getCost());
    }

    @Test
    void givenAValidValuesWithoutShippingIdAndCost_whenCallCreate_thenReturnOrderShippingDetails() {
        final var aShippingCompany = "shippingCompany";
        final var aShippingType = "shippingType";

        final var aOrderShippingDetails = OrderShippingDetails.create(
                aShippingCompany,
                aShippingType
        );

        Assertions.assertNotNull(aOrderShippingDetails);
        Assertions.assertTrue(aOrderShippingDetails.getShippingId().isEmpty());
        Assertions.assertEquals(aShippingCompany, aOrderShippingDetails.getShippingCompany());
        Assertions.assertEquals(aShippingType, aOrderShippingDetails.getShippingType());
        Assertions.assertEquals(BigDecimal.ZERO, aOrderShippingDetails.getCost());
    }

    @Test
    void givenAnInvalidEmptyShippingId_whenCallCreate_thenThrowNotificationException() {
        final String aShippingId = "";
        final var aShippingCompany = "shippingCompany";
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var expectedErrorMessage = "'shippingId' should not be empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderShippingDetails.create(
                    aShippingId,
                    aShippingCompany,
                    aShippingType,
                    aCost
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullShippingCompany_whenCallCreate_thenThrowNotificationException() {
        final var aShippingId = "shippingId";
        final String aShippingCompany = null;
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var expectedErrorMessage = "'shippingCompany' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderShippingDetails.create(
                    aShippingId,
                    aShippingCompany,
                    aShippingType,
                    aCost
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyShippingCompany_whenCallCreate_thenThrowNotificationException() {
        final var aShippingId = "shippingId";
        final var aShippingCompany = "";
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var expectedErrorMessage = "'shippingCompany' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderShippingDetails.create(
                    aShippingId,
                    aShippingCompany,
                    aShippingType,
                    aCost
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullShippingType_whenCallCreate_thenThrowNotificationException() {
        final var aShippingId = "shippingId";
        final var aShippingCompany = "shippingCompany";
        final String aShippingType = null;
        final var aCost = new BigDecimal("10.00");

        final var expectedErrorMessage = "'shippingType' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderShippingDetails.create(
                    aShippingId,
                    aShippingCompany,
                    aShippingType,
                    aCost
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyShippingType_whenCallCreate_thenThrowNotificationException() {
        final var aShippingId = "shippingId";
        final var aShippingCompany = "shippingCompany";
        final String aShippingType = "";
        final var aCost = new BigDecimal("10.00");

        final var expectedErrorMessage = "'shippingType' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class, () -> {
            OrderShippingDetails.create(
                    aShippingId,
                    aShippingCompany,
                    aShippingType,
                    aCost
            );
        });

        Assertions.assertEquals(expectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void testCallToStringInOrderShippingDetails() {
        final var aShippingId = "shippingId";
        final var aShippingCompany = "shippingCompany";
        final var aShippingType = "shippingType";
        final var aCost = new BigDecimal("10.00");

        final var aOrderShippingDetails = OrderShippingDetails.create(
                aShippingId,
                aShippingCompany,
                aShippingType,
                aCost
        );

        Assertions.assertNotNull(aOrderShippingDetails.toString());
    }
}
