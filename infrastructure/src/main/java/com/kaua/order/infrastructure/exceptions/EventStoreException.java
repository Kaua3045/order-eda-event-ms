package com.kaua.order.infrastructure.exceptions;

import com.kaua.order.domain.exceptions.NoStackTraceException;

public class EventStoreException extends NoStackTraceException {

    private EventStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public static EventStoreException with(String message) {
        return new EventStoreException(message, null);
    }
}
