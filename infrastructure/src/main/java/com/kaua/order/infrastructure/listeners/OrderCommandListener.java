package com.kaua.order.infrastructure.listeners;

import com.kaua.order.application.handlers.commands.CreateOrderCommand;
import com.kaua.order.application.handlers.create.AsyncCreateOrderHandler;
import com.kaua.order.infrastructure.configurations.json.Json;
import com.kaua.order.infrastructure.constants.HeadersConstants;
import com.kaua.order.infrastructure.exceptions.KafkaHeadersException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderCommandListener extends EventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCommandListener.class);

    private static final String ORDER_DLT_INVALID = "order-commands-dlt-invalid";

    private final AsyncCreateOrderHandler asyncCreateOrderHandler;

    public OrderCommandListener(
            final KafkaTemplate<String, Object> kafkaTemplate,
            final AsyncCreateOrderHandler asyncCreateOrderHandler
    ) {
        super(kafkaTemplate);
        this.asyncCreateOrderHandler = Objects.requireNonNull(asyncCreateOrderHandler);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.orders-commands.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = {
                    "${kafka.consumers.orders-commands.topics.[0]}",
            },
            groupId = "${kafka.consumers.orders-commands.group-id}",
            // generate a random id for the consumer
            id = "${kafka.consumers.orders-commands.id}-#{T(java.util.UUID).randomUUID().toString()}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.orders-commands.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            // backoff delay 2 seconds, multiplier 2
            backoff = @Backoff(delay = 2000, multiplier = 2),
            attempts = "${kafka.consumers.orders-commands.max-attempts}",
            autoCreateTopics = "${kafka.consumers.orders-commands.auto-create-topics}",
            dltTopicSuffix = "-retry-dlt",
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    public void onMessage(
            @Payload final ConsumerRecord<String, String> record,
            final Acknowledgment ack
    ) {
        log.atLevel(Level.INFO).log("Message received from Kafka [topic:{}] [partition:{}] [offset:{}]: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        try {
            // TODO implement check inbox table to avoid processing duplicated messages
            final var aCommandType = getTypeHeaderValue(record, HeadersConstants.COMMAND_TYPE);

            final var aPayload = record.value();

            switch (aCommandType) {
                case CreateOrderCommand.COMMAND_TYPE -> {
                    log.debug("Handling CreateOrderCommand");
                    final var aCommand = Json.readValue(aPayload, CreateOrderCommand.class);

                    log.debug("Deserialized command and handling: {}", aCommand);
                    this.asyncCreateOrderHandler.handle(aCommand);
                    ack.acknowledge();
                    log.info("CreateOrderCommand processed {}", aCommand);
                }
                default -> handleMessagingTypeNotSupported(
                        record,
                        aCommandType,
                        ORDER_DLT_INVALID,
                        ack,
                        log,
                        HeadersConstants.COMMAND_OCCURRED_ON,
                        HeadersConstants.COMMAND_TYPE,
                        HeadersConstants.COMMAND_ID
                );
            }
        } catch (final KafkaHeadersException ex) {
            handleMessagingNotContainsHeaders(
                    record,
                    ORDER_DLT_INVALID,
                    ack,
                    log,
                    ex.getMessage()
            );
        } catch (final Exception ex) {
            // TODO: delete message from inbox table
            // and retry message
            throw ex;
        }
    }

    @DltHandler
    public void onDltMessage(
            @Payload ConsumerRecord<String, String> record,
            final Acknowledgment acknowledgment
    ) {
        log.atLevel(Level.WARN).log("Message received from Kafka at DLT [topic:{}] [partition:{}] [offset:{}]: {}",
                record.topic(), record.partition(), record.offset(), record.value());

        final var aTopicRetry = new String(record.headers().lastHeader("kafka_original-topic").value());

        log.debug("Retrying message from DLT [topic:{}] [partition:{}] [offset:{}]: {}",
                aTopicRetry, record.partition(), record.offset(), record.value());

        handlePublishMessageToRetryTopic(
                record,
                aTopicRetry,
                acknowledgment,
                log,
                HeadersConstants.COMMAND_TYPE,
                HeadersConstants.COMMAND_ID
        );
    }
}
