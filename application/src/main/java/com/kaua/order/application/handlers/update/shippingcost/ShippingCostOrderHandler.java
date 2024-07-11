package com.kaua.order.application.handlers.update.shippingcost;

import com.kaua.order.application.EventHandler;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.NotFoundException;
import com.kaua.order.domain.order.Order;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;

import java.util.List;
import java.util.Objects;

public class ShippingCostOrderHandler extends EventHandler<ShippingCostCalculatedEvent> {

    private final EventStore eventStore;

    public ShippingCostOrderHandler(final EventStore eventStore) {
        this.eventStore = Objects.requireNonNull(eventStore);
    }

    @Override
    public void handle(final ShippingCostCalculatedEvent aEvent) {
        List<DomainEvent> aAggregateEvents = this.eventStore.loadEvents(aEvent.aggregateId());

        if (aAggregateEvents.isEmpty()) {
            throw NotFoundException.with("Order", aEvent.aggregateId()).get();
        }

        final var aOrder = Order.reconstruct(aAggregateEvents);
        aOrder.handle(aEvent);

        this.eventStore.save(aOrder);
    }
}
