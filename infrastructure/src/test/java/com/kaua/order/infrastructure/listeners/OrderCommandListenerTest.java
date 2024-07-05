package com.kaua.order.infrastructure.listeners;

import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.commands.CreateOrderItemCommand;
import com.kaua.order.application.handlers.create.AsyncCreateOrderHandler;
import com.kaua.order.domain.commands.InternalCommand;
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

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OrderCommandListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private AsyncCreateOrderHandler asyncCreateOrderHandler;

    @SpyBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private OrderCommandListener orderCommandListener;

    @Value("${kafka.consumers.orders-commands.topics.[0]}")
    private String topic;

    @Captor
    private ArgumentCaptor<ConsumerRecord<String, String>> record;

    @BeforeEach
    void clean() throws ExecutionException, InterruptedException, TimeoutException {
        cleanUpMessages("command");
    }

    @Test
    void givenAValidCreateOrderCommand_whenReceiveThrowsExceptionAndSendToDLT_shouldResendToPrimaryTopic() throws InterruptedException, ExecutionException, TimeoutException {
        final var expectedMaxAttempts = 4;
        final var expectedMaxDltAttempts = 1;
        final var expectedMainTopic = "place_order-command-topic";
        final var expectedRetry0Topic = "place_order-command-topic-retry-0";
        final var expectedRetry1Topic = "place_order-command-topic-retry-1";
        final var expectedRetry2Topic = "place_order-command-topic-retry-2";
        final var expectedDltTopic = "place_order-command-topic-retry-dlt";

        final var aCreateOrderCommand = CreateOrderCommand.with(
                "customerId",
                Set.of(
                        CreateOrderItemCommand.with("sku", 1)
                ),
                "couponCode",
                "paymentMethodId",
                1,
                "shippingCompany",
                "shippingType",
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aCreateOrderCommand);

        final var latch = new CountDownLatch(5);

        Mockito.doAnswer(t -> {
            latch.countDown();
            throw new RuntimeException("Error on handle create order command");
        }).when(asyncCreateOrderHandler).handle(Mockito.any());

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(orderCommandListener).onDltMessage(Mockito.any(), Mockito.any());

        final var aProducerRecord = createProducerRecord(
                topic,
                aMessage,
                aCreateOrderCommand,
                HeadersConstants.COMMAND_ID,
                HeadersConstants.COMMAND_TYPE,
                HeadersConstants.COMMAND_OCCURRED_ON
        );
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));

        Mockito.verify(orderCommandListener, Mockito.times(expectedMaxAttempts)).onMessage(record.capture(), Mockito.any());

        final var allMetas = record.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        Mockito.verify(orderCommandListener, Mockito.times(expectedMaxDltAttempts)).onDltMessage(record.capture(), Mockito.any());

        Assertions.assertEquals(expectedDltTopic, record.getValue().topic());
    }

    @Test
    void givenAValidCreateOrderCommand_whenReceive_shouldCreateOrder() throws Exception {
        // given
        final var aCreateOrderCommand = CreateOrderCommand.with(
                "customerId",
                Set.of(
                        CreateOrderItemCommand.with("sku", 1)
                ),
                "couponCode",
                "paymentMethodId",
                1,
                "shippingCompany",
                "shippingType",
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aCreateOrderCommand);

        final var latch = new CountDownLatch(1);

        Mockito.doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(asyncCreateOrderHandler).handle(Mockito.any());

        // when
        final var aProducerRecord = createProducerRecord(
                topic,
                aMessage,
                aCreateOrderCommand,
                HeadersConstants.COMMAND_ID,
                HeadersConstants.COMMAND_TYPE,
                HeadersConstants.COMMAND_OCCURRED_ON
        );
        producer().send(aProducerRecord).get(1, TimeUnit.MINUTES);

        Assertions.assertTrue(latch.await(3, TimeUnit.MINUTES));
    }

    @Test
    void givenAValidCommandButCommandTypeDoesNotMatch_whenReceive_shouldDoNothing() throws ExecutionException, InterruptedException, TimeoutException {
        // given
        final var aMockedAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aCommand = TestListenerCommand.with(IdUtils.generateIdWithoutHyphen());
        final var aMessage = Json.writeValueAsString(aCommand);

        final var aLatch = new CountDownLatch(1);

        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockedAcknowledgment).nack(Mockito.any(Duration.class));

        // when
        final var aConsumerRecord = createConsumerRecord(
                topic,
                aMessage,
                aCommand,
                HeadersConstants.COMMAND_ID,
                HeadersConstants.COMMAND_TYPE,
                HeadersConstants.COMMAND_OCCURRED_ON
        );
        final var aOrderCommandListener = new OrderCommandListener(
                kafkaTemplate,
                asyncCreateOrderHandler
        );
        aOrderCommandListener.onMessage(aConsumerRecord, aMockedAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
    }

    @Test
    void givenAValidCreateOrderCommand_whenReceiveInDLTAndSendToTopic_shouldResendToTopic() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aCreateOrderCommand = CreateOrderCommand.with(
                "customerId",
                Set.of(
                        CreateOrderItemCommand.with("sku", 1)
                ),
                "couponCode",
                "paymentMethodId",
                1,
                "shippingCompany",
                "shippingType",
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aCreateOrderCommand);

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = createConsumerRecord(
                topic,
                aMessage,
                aCreateOrderCommand,
                HeadersConstants.COMMAND_ID,
                HeadersConstants.COMMAND_TYPE,
                HeadersConstants.COMMAND_OCCURRED_ON
        );
        final var aOrderCommandListener = new OrderCommandListener(
                kafkaTemplate,
                asyncCreateOrderHandler
        );
        aOrderCommandListener.onDltMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(2, TimeUnit.MINUTES));
        Mockito.verify(asyncCreateOrderHandler, Mockito.times(0))
                .handle(Mockito.any());
    }

    @Test
    void givenAValidCreateOrderCommandButNotContainsHeaders_whenReceiveAndSendToDlt_shouldNotProcessIt() throws Exception {
        // given
        final var aMockAcknowledgment = Mockito.mock(Acknowledgment.class);

        final var aCreateOrderCommand = CreateOrderCommand.with(
                "customerId",
                Set.of(
                        CreateOrderItemCommand.with("sku", 1)
                ),
                "couponCode",
                "paymentMethodId",
                1,
                "shippingCompany",
                "shippingType",
                "who",
                "traceId"
        );

        final var aMessage = Json.writeValueAsString(aCreateOrderCommand);

        final var aLatch = new CountDownLatch(1);

        // when
        Mockito.doAnswer(it -> {
            aLatch.countDown();
            return null;
        }).when(aMockAcknowledgment).acknowledge();

        final var aConsumerRecord = new ConsumerRecord<String, String>(topic, 1, 0, null, aMessage);
        aConsumerRecord.headers().add(HeadersConstants.COMMAND_ID, aCreateOrderCommand.commandId().getBytes());
        aConsumerRecord.headers().add(HeadersConstants.COMMAND_OCCURRED_ON, aCreateOrderCommand.occurredOn().toString().getBytes());
        final var aOrderCommandListener = new OrderCommandListener(
                kafkaTemplate,
                asyncCreateOrderHandler
        );
        aOrderCommandListener.onMessage(aConsumerRecord, aMockAcknowledgment);

        // then
        Assertions.assertTrue(aLatch.await(3, TimeUnit.MINUTES));
        Mockito.verify(asyncCreateOrderHandler, Mockito.times(0))
                .handle(Mockito.any());
    }

    record TestListenerCommand(
            String id,
            String commandId,
            String commandType,
            Instant occurredOn,
            String who,
            String traceId
    ) implements InternalCommand {

        public static TestListenerCommand with(
                String id
        ) {
            return new TestListenerCommand(
                    id,
                    IdUtils.generateIdWithoutHyphen(),
                    "test-command", InstantUtils.now(), "teste", "1");
        }
    }
}
