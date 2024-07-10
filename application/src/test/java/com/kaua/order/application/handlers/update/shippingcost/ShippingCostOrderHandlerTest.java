package com.kaua.order.application.handlers.update.shippingcost;

import com.kaua.order.application.EventHandlerTest;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.NotFoundException;
import com.kaua.order.domain.order.OrderItem;
import com.kaua.order.domain.order.OrderStatus;
import com.kaua.order.domain.order.events.OrderCreationInitiatedEvent;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;
import com.kaua.order.domain.order.valueobjects.OrderPaymentDetails;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ShippingCostOrderHandlerTest extends EventHandlerTest {

    @Mock
    private EventStore eventStore;

    @InjectMocks
    private ShippingCostOrderHandler shippingCostOrderHandler;

    @Test
    void givenAValidEvent_whenCallHandle_thenShouldUpdateOrderShippingCost() {
        final var aOrderCreation = OrderCreationInitiatedEvent.from(
                "1",
                OrderStatus.CREATION_INITIATED.name(),
                "2",
                BigDecimal.TEN,
                Set.of(OrderItem.create(
                        "12345-sku",
                        1,
                        BigDecimal.TEN
                )),
                Fixture.address(null),
                null,
                OrderPaymentDetails.create("1", 1),
                OrderShippingDetails.create("Correios", "SEDEX"),
                0,
                "2",
                IdUtils.generateIdWithoutHyphen()
        );

        final var aEvent = ShippingCostCalculatedEvent.from(
                aOrderCreation.aggregateId(),
                aOrderCreation.orderStatus(),
                aOrderCreation.totalAmount(),
                aOrderCreation.shippingAddress(),
                OrderShippingDetails.create(
                        "Correios",
                        "SEDEX",
                        BigDecimal.TWO
                ),
                aOrderCreation.aggregateVersion(),
                aOrderCreation.customerId(),
                aOrderCreation.traceId()
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreation);

        Mockito.when(eventStore.loadEvents(aEvent.aggregateId()))
                .thenReturn(aEvents);
        Mockito.doNothing().when(eventStore).save(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.shippingCostOrderHandler.handle(aEvent));

        Mockito.verify(eventStore, Mockito.times(1)).loadEvents(aEvent.aggregateId());
        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnNonExistsAggregateId_whenCallHandle_thenShouldThrowException() {
        final var aEvent = ShippingCostCalculatedEvent.from(
                "1",
                OrderStatus.CREATION_INITIATED.name(),
                BigDecimal.TEN,
                Fixture.address(null),
                OrderShippingDetails.create(
                        "Correios",
                        "SEDEX",
                        BigDecimal.TWO
                ),
                0,
                "2",
                IdUtils.generateIdWithoutHyphen()
        );

        Mockito.when(eventStore.loadEvents(aEvent.aggregateId()))
                .thenReturn(List.of());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.shippingCostOrderHandler.handle(aEvent));

        Assertions.assertEquals("Order with id 1 was not found", aException.getMessage());

        Mockito.verify(eventStore, Mockito.times(1)).loadEvents(aEvent.aggregateId());
        Mockito.verify(eventStore, Mockito.times(0)).save(Mockito.any());
    }
}
