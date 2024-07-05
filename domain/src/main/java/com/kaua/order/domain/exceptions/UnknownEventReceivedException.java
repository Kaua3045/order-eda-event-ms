package com.kaua.order.domain.exceptions;

import java.util.Collections;

public class UnknownEventReceivedException extends DomainException {

    public UnknownEventReceivedException(final String eventType) {
        super("unknown event %s received and cannot be processed".formatted(eventType), Collections.emptyList());
    }
}
