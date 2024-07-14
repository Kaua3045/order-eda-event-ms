package com.kaua.order.application.handlers.update.paymenttax;

import com.kaua.order.application.EventHandler;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.NotFoundException;
import com.kaua.order.domain.order.Order;
import com.kaua.order.domain.order.events.external.PaymentTaxCalculatedEvent;

import java.util.List;
import java.util.Objects;

public class PaymentTaxOrderHandler extends EventHandler<PaymentTaxCalculatedEvent> {

    private final EventStore eventStore;

    public PaymentTaxOrderHandler(final EventStore eventStore) {
        this.eventStore = Objects.requireNonNull(eventStore);
    }

    @Override
    public void handle(final PaymentTaxCalculatedEvent aEvent) {
        List<DomainEvent> aAggregateEvents = this.eventStore.loadEvents(aEvent.aggregateId());

        if (aAggregateEvents.isEmpty()) {
            throw NotFoundException.with("Order", aEvent.aggregateId()).get();
        }

        final var aOrder = Order.reconstruct(aAggregateEvents);
        aOrder.handle(aEvent);

        this.eventStore.save(aOrder);
    }
}
