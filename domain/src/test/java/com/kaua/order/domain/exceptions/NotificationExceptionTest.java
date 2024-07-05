package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotificationExceptionTest extends UnitTest {

    @Test
    void givenAValidValues_whenCallNewNotificationException_thenNotificationExceptionIsCreated() {
        final var aNotificationHandler = NotificationHandler.create(new Error("'param' is required"));

        final var aNotificationException = new NotificationException("Invalid parameters", aNotificationHandler);

        Assertions.assertNotNull(aNotificationException);
        Assertions.assertEquals(1, aNotificationException.getErrors().size());
    }
}
