package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.validation.handler.NotificationHandler;

public class NotificationException extends DomainException {

    public NotificationException(final String aMessage, final NotificationHandler notification) {
        super(aMessage, notification.getErrors());
    }
}
