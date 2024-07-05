package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderAddressTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallCreate_thenReturnOrderAddress() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aOrderAddress = OrderAddress.create(
                aStreet,
                aNumber,
                aComplement,
                aCity,
                aState,
                aZipCode
        );

        Assertions.assertNotNull(aOrderAddress);
        Assertions.assertEquals(aStreet, aOrderAddress.getStreet());
        Assertions.assertEquals(aNumber, aOrderAddress.getNumber());
        Assertions.assertEquals(aComplement, aOrderAddress.getComplement().get());
        Assertions.assertEquals(aCity, aOrderAddress.getCity());
        Assertions.assertEquals(aState, aOrderAddress.getState());
        Assertions.assertEquals(aZipCode, aOrderAddress.getZipCode());
    }

    @Test
    void givenAValidValuesWithoutComplement_whenCallCreate_thenReturnOrderAddress() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aOrderAddress = OrderAddress.create(
                aStreet,
                aNumber,
                null,
                aCity,
                aState,
                aZipCode
        );

        Assertions.assertNotNull(aOrderAddress);
        Assertions.assertEquals(aStreet, aOrderAddress.getStreet());
        Assertions.assertEquals(aNumber, aOrderAddress.getNumber());
        Assertions.assertFalse(aOrderAddress.getComplement().isPresent());
        Assertions.assertEquals(aCity, aOrderAddress.getCity());
        Assertions.assertEquals(aState, aOrderAddress.getState());
        Assertions.assertEquals(aZipCode, aOrderAddress.getZipCode());
    }

    @Test
    void givenAnInvalidNullStreet_whenCallCreate_thenThrowNotificationException() {
        final String aStreet = null;
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'street' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyStreet_whenCallCreate_thenThrowNotificationException() {
        final String aStreet = "";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'street' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullNumber_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final String aNumber = null;
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'number' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyNumber_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final String aNumber = "";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'number' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyComplement_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final String aComplement = "";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'complement' should not be empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullCity_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final String aCity = null;
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'city' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyCity_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final String aCity = "";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'city' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullState_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final String aState = null;
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'state' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyState_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final String aState = "";
        final var aZipCode = "zipCode";

        final var aExpectedErrorMessage = "'state' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullZipCode_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final String aZipCode = null;

        final var aExpectedErrorMessage = "'zipCode' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidEmptyZipCode_whenCallCreate_thenThrowNotificationException() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final String aZipCode = "";

        final var aExpectedErrorMessage = "'zipCode' should not be null or empty";

        final var aException = Assertions.assertThrows(NotificationException.class,
                () -> OrderAddress.create(
                        aStreet,
                        aNumber,
                        aComplement,
                        aCity,
                        aState,
                        aZipCode)
        );

        Assertions.assertEquals(aExpectedErrorMessage, aException.getErrors().get(0).message());
    }

    @Test
    void testCallToStringInOrderAddress() {
        final var aStreet = "street";
        final var aNumber = "number";
        final var aComplement = "complement";
        final var aCity = "city";
        final var aState = "state";
        final var aZipCode = "zipCode";

        final var aOrderAddress = OrderAddress.create(
                aStreet,
                aNumber,
                aComplement,
                aCity,
                aState,
                aZipCode
        );

        Assertions.assertNotNull(aOrderAddress.toString());
    }
}
