package com.kaua.order.infrastructure.commands;

import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.commands.CreateOrderItemCommand;
import com.kaua.order.infrastructure.UnitTest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@UnitTest
public class KafkaCommandBusTest {

    @Test
    void givenAValidCommandAndTopic_whenDispatch_thenShouldReturnVoid() {
        final var aKafkaTemplate = Mockito.mock(KafkaTemplate.class);

        final var aCommand = CreateOrderCommand.with(
                "1",
                Set.of(CreateOrderItemCommand.with("1", 1)),
                "COUPON",
                "1",
                1,
                "SHIPPING_COMPANY",
                "SHIPPING_TYPE",
                "1",
                "1"
        );
        final var aTopic = "TOPIC";

        final var kafkaCommandBus = new KafkaCommandBus(aKafkaTemplate);

        CompletableFuture<SendResult<String, Object>> successFuture = new CompletableFuture<>();
        successFuture.complete(Mockito.mock(SendResult.class));

        Mockito.when(aKafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(successFuture);

        Assertions.assertDoesNotThrow(() -> kafkaCommandBus.dispatch(aCommand, aTopic));

        Mockito.verify(aKafkaTemplate, Mockito.times(1)).send(Mockito.any(ProducerRecord.class));
    }

    @Test
    void givenAValidCommandAndTopic_whenDispatch_thenShouldThrowRuntimeException() {
        final var aKafkaTemplate = Mockito.mock(KafkaTemplate.class);

        final var aCommand = CreateOrderCommand.with(
                "1",
                Set.of(CreateOrderItemCommand.with("1", 1)),
                "COUPON",
                "1",
                1,
                "SHIPPING_COMPANY",
                "SHIPPING_TYPE",
                "1",
                "1"
        );
        final var aTopic = "TOPIC";

        final var kafkaCommandBus = new KafkaCommandBus(aKafkaTemplate);

        CompletableFuture<SendResult<String, Object>> successFuture = new CompletableFuture<>();
        successFuture.completeExceptionally(new RuntimeException());

        Mockito.when(aKafkaTemplate.send(Mockito.any(ProducerRecord.class))).thenReturn(successFuture);

        Assertions.assertDoesNotThrow(() -> kafkaCommandBus.dispatch(aCommand, aTopic));

        Mockito.verify(aKafkaTemplate, Mockito.times(1)).send(Mockito.any(ProducerRecord.class));
    }
}
