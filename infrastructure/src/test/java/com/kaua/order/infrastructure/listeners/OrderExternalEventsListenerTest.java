package com.kaua.order.infrastructure.listeners;

import com.kaua.order.application.handlers.update.shippingcost.ShippingCostOrderHandler;
import com.kaua.order.domain.Fixture;
import com.kaua.order.domain.events.DomainEvent;
import com.kaua.order.domain.order.OrderStatus;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;
import com.kaua.order.domain.order.valueobjects.OrderShippingDetails;
import com.kaua.order.domain.utils.IdUtils;
import com.kaua.order.domain.utils.InstantUtils;
import com.kaua.order.infrastructure.AbstractEmbeddedKafkaTest;
import com.kaua.order.infrastructure.configurations.json.Json;
import com.kaua.order.infrastructure.constants.HeadersConstants;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderExternalEventsListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private ShippingCostOrderHandler shippingCostOrderHandler;

    @SpyBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private OrderExternalEventsListener orderExternalEventsListener;

    @Value("${kafka.consumers.orders-external-events.topics.[0]}")
    private String shippingCostCalculatedTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecord<String, String>> record;

    @BeforeEach
    void clean() throws ExecutionException, InterruptedException, TimeoutException {
        cleanUpMessages("external-event");
    }

    @Test
    void givenAValidShippingCostCalculatedEvent_whenReceiveThrowsExceptionAndSendToDLT_shouldResendToPrimaryTopic() throws InterruptedException, ExecutionException, TimeoutException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "shipping-cost-calculated-external-event-topic";
        final var expectedRetry0Topic = "shipping-cost-calculated-external-event-topic-retry-0";
        final var expectedRetry1Topic = "shipping-cost-calculated-external-event-topic-retry-1";
        final var expectedRetry2Topic = "shipping-cost-calculated-external-event-topic-retry-2";
        final var expectedDltTopic = "shipping-cost-calculated-external-event-topic-retry-dlt";

        final var aShippingCostCalculatedEvent = ShippingCostCalculatedEvent.from(
                IdUtils.generateIdWithoutHyphen(),
                OrderStatus.CREATION_INITIATED.name(),
                new BigDecimal("10.00"),
                Fixture.address(null),
                OrderShippingDetails.create(
                        "CORREIOS",
                        "SEDEX",
                        BigDecimal.TEN
                ),
                0,
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aShippingCostCalculatedEvent);

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on handle create order command");
        }).when(shippingCostOrderHandler).handle(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(orderExternalEventsListener).onDltMessage(Mockito.any(), Mockito.any());

        final var aProducerRecord = createProducerRecord(
                shippingCostCalculatedTopic,
                aMessage,
                aShippingCostCalculatedEvent
        );
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(orderExternalEventsListener, Mockito.times(expectedMaxAttempts)).onMessage(record.capture(), Mockito.any());

        final var allMetas = record.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(orderExternalEventsListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(record.capture(), Mockito.any());

        Assertions.assertEquals(expectedDltTopic, record.getValue().topic());
    }

    @Test
    void givenAValidShippingCostCalculatedEvent_whenReceive_shouldApplyCost() throws Exception {
        // given
        final var aShippingCostCalculatedEvent = ShippingCostCalculatedEvent.from(
                IdUtils.generateIdWithoutHyphen(),
                OrderStatus.CREATION_INITIATED.name(),
                new BigDecimal("10.00"),
                Fixture.address(null),
                OrderShippingDetails.create(
                        "CORREIOS",
                        "SEDEX",
                        BigDecimal.TEN
                ),
                0,
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aShippingCostCalculatedEvent);

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(shippingCostOrderHandler).handle(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(
                shippingCostCalculatedTopic,
                aMessage,
                aShippingCostCalculatedEvent
        );
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));
    }

    @Test
    void givenAValidCommandButEventTypeDoesNotMatch_whenReceive_shouldDoNothing() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aMockedAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aEvent = TestListenerEvent.with(IdUtils.generateIdWithoutHyphen());
        final var aMessage = Json.writeValueAsString(aEvent);

        final var aLatch = new CountDownLatch(1);

        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAcknowledgment).nack(Mockito.any(Duration.class));

        // when
        final var aConsumerRecord = createConsumerRecord(
                toString(),
                aMessage,
                aEvent
        );
        final var aOrderCommandListener = new OrderExternalEventsListener(
                kafkaTemplate,
                shippingCostOrderHandler
        );
        aOrderCommandListener.onMessage(aConsumerRecord, aMockedAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
    }

    @Test
    void givenAValidShippingCostCalculatedEvent_whenReceiveInDLTAndSendToTopic_shouldResendToTopic() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aShippingCostCalculatedEvent = ShippingCostCalculatedEvent.from(
                IdUtils.generateIdWithoutHyphen(),
                OrderStatus.CREATION_INITIATED.name(),
                new BigDecimal("10.00"),
                Fixture.address(null),
                OrderShippingDetails.create(
                        "CORREIOS",
                        "SEDEX",
                        BigDecimal.TEN
                ),
                0,
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aShippingCostCalculatedEvent);

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = createConsumerRecord(
                shippingCostCalculatedTopic,
                aMessage,
                aShippingCostCalculatedEvent
        );
        final var aOrderCommandListener = new OrderExternalEventsListener(
                kafkaTemplate,
                shippingCostOrderHandler
        );
        aOrderCommandListener.onDltMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(2, TimeUnit.MINUTES));
        Mockito.verify(shippingCostOrderHandler, Mockito.times(0))
                .handle(Mockito.any());
    }

    @Test
    void givenAValidShippingCostCalculatedEventButNotContainsHeaders_whenReceiveAndSendToDlt_shouldNotProcessIt() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aShippingCostCalculatedEvent = ShippingCostCalculatedEvent.from(
                IdUtils.generateIdWithoutHyphen(),
                OrderStatus.CREATION_INITIATED.name(),
                new BigDecimal("10.00"),
                Fixture.address(null),
                OrderShippingDetails.create(
                        "CORREIOS",
                        "SEDEX",
                        BigDecimal.TEN
                ),
                0,
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aShippingCostCalculatedEvent);

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = new ConsumerRecord<String, String>(shippingCostCalculatedTopic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add(HeadersConstants.EVENT_ID, aShippingCostCalculatedEvent.eventId().getBytes());
        aConsumerRecord.headers().add(HeadersConstants.COMMAND_OCCURRED_ON, aShippingCostCalculatedEvent.occurredOn().toString().getBytes());
        final var aOrderCommandListener = new OrderExternalEventsListener(
                kafkaTemplate,
                shippingCostOrderHandler
        );
        aOrderCommandListener.onMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(shippingCostOrderHandler, Mockito.times(0))
                .handle(Mockito.any());
    }

    record TestListenerEvent(
            String aggregateId,
            String eventId,
            String eventType,
            String eventClassName,
            Instant occurredOn,
            long aggregateVersion,
            String who,
            String traceId
    ) implements DomainEvent {

        public static TestListenerEvent with(
                String aggregateId
        ) {
            return new TestListenerEvent(
                    aggregateId,
                    IdUtils.generateIdWithoutHyphen(),
                    "test-event",
                    TestListenerEvent.class.getName(),
                    InstantUtils.now(),
                    1,
                    "teste",
                    "1");
        }
    }
}
