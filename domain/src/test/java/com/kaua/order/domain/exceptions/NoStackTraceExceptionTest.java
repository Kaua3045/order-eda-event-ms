package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoStackTraceExceptionTest extends UnitTest {

    @Test
    void testCallNoStacktraceExceptionWithoutCause() {
        final var exception = new NoStackTraceException("message");

        Assertions.assertEquals("message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void testCallNoStacktraceExceptionWithCause() {
        final var cause = new RuntimeException();
        final var exception = new NoStackTraceException("message", cause);

        Assertions.assertEquals("message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}
