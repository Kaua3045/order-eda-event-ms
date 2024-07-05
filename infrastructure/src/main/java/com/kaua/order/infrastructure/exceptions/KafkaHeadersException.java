package com.kaua.order.infrastructure.exceptions;

import com.kaua.order.domain.exceptions.NoStackTraceException;

public class KafkaHeadersException extends NoStackTraceException {

    private KafkaHeadersException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static KafkaHeadersException with(final String message) {
        return new KafkaHeadersException(message, null);
    }
}
