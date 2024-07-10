package com.kaua.order.application;

import com.kaua.order.domain.events.DomainEvent;

public abstract class EventHandler<T extends DomainEvent> {

    public abstract void handle(final T aEvent);
}
