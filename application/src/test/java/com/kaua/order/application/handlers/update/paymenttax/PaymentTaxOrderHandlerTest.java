package com.kaua.order.application.handlers.update.paymenttax;

import com.kaua.order.application.EventHandlerTest;
import com.kaua.order.application.repositories.EventStore;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.exceptions.NotFoundException;
import com.kaua.order.domain.order.OrderItem;
import com.kaua.order.domain.order.OrderStatus;
import com.kaua.order.domain.order.events.OrderCreationInitiatedEvent;
import com.kaua.order.domain.order.events.OrderShippingCostCalculatedEvent;
import com.kaua.order.domain.order.events.external.PaymentTaxCalculatedEvent;
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

public class PaymentTaxOrderHandlerTest extends EventHandlerTest {

    @Mock
    private EventStore eventStore;

    @InjectMocks
    private PaymentTaxOrderHandler paymentTaxOrderHandler;

    @Test
    void givenAValidEvent_whenCallHandle_thenShouldUpdateOrderPaymentTax() {
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

        final var aOrderShippingCostCalculated = OrderShippingCostCalculatedEvent.from(
                aOrderCreation.aggregateId(),
                aOrderCreation.orderStatus(),
                new BigDecimal("12.00"),
                aOrderCreation.shippingAddress(),
                aOrderCreation.paymentDetails(),
                OrderShippingDetails.create(
                        "Correios",
                        "SEDEX",
                        new BigDecimal("2.00")
                ),
                1,
                aOrderCreation.customerId(),
                aOrderCreation.traceId()
        );

        final var aEvent = PaymentTaxCalculatedEvent.from(
                aOrderCreation.aggregateId(),
                aOrderCreation.orderStatus(),
                new BigDecimal("12.00"),
                OrderPaymentDetails.create(
                        aOrderCreation.paymentDetails().paymentMethodId(),
                        aOrderCreation.paymentDetails().installments(),
                        new BigDecimal("3.00")
                ),
                aOrderShippingCostCalculated.aggregateVersion(),
                aOrderCreation.customerId(),
                aOrderCreation.traceId()
        );

        final var aEvents = new ArrayList<DomainEvent>();
        aEvents.add(aOrderCreation);
        aEvents.add(aOrderShippingCostCalculated);

        Mockito.when(eventStore.loadEvents(aEvent.aggregateId()))
                .thenReturn(aEvents);
        Mockito.doNothing().when(eventStore).save(Mockito.any());

        Assertions.assertDoesNotThrow(() -> this.paymentTaxOrderHandler.handle(aEvent));

        Mockito.verify(eventStore, Mockito.times(1)).loadEvents(aEvent.aggregateId());
        Mockito.verify(eventStore, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void givenAnNonExistsAggregateId_whenCallHandle_thenShouldThrowException() {
        final var aEvent = PaymentTaxCalculatedEvent.from(
                "1",
                OrderStatus.SHIPPING_CALCULATED.name(),
                new BigDecimal("12.00"),
                OrderPaymentDetails.create(
                        "2",
                        1,
                        new BigDecimal("3.00")
                ),
                1,
                "123",
                "1012323"
        );

        Mockito.when(eventStore.loadEvents(aEvent.aggregateId()))
                .thenReturn(List.of());

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> this.paymentTaxOrderHandler.handle(aEvent));

        Assertions.assertEquals("Order with id 1 was not found", aException.getMessage());

        Mockito.verify(eventStore, Mockito.times(1)).loadEvents(aEvent.aggregateId());
        Mockito.verify(eventStore, Mockito.times(0)).save(Mockito.any());
    }
}
