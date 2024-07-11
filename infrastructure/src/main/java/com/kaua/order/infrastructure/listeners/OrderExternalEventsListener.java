package com.kaua.order.infrastructure.listeners;

import com.kaua.order.application.handlers.update.shippingcost.ShippingCostOrderHandler;
import com.kaua.order.domain.order.events.external.ShippingCostCalculatedEvent;
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
public class OrderExternalEventsListener extends EventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderExternalEventsListener.class);

    private static final String ORDER_DLT_INVALID = "order-external-events-dlt-invalid";

    private final ShippingCostOrderHandler shippingCostOrderHandler;

    public OrderExternalEventsListener(
            final KafkaTemplate<String, Object> kafkaTemplate,
            final ShippingCostOrderHandler shippingCostOrderHandler
    ) {
        super(kafkaTemplate);
        this.shippingCostOrderHandler = Objects.requireNonNull(shippingCostOrderHandler);
    }

    @KafkaListener(
            concurrency = "${kafka.consumers.orders-external-events.concurrency}",
            containerFactory = "kafkaListenerFactory",
            topics = {
                    "${kafka.consumers.orders-external-events.topics.[0]}",
            },
            groupId = "${kafka.consumers.orders-external-events.group-id}",
            // generate a random id for the consumer
            id = "${kafka.consumers.orders-external-events.id}-#{T(java.util.UUID).randomUUID().toString()}",
            properties = {
                    "auto.offset.reset=${kafka.consumers.orders-external-events.auto-offset-reset}"
            }
    )
    @RetryableTopic(
            // backoff delay 2 seconds, multiplier 2
            backoff = @Backoff(delay = 2000, multiplier = 2),
            attempts = "${kafka.consumers.orders-external-events.max-attempts}",
            autoCreateTopics = "${kafka.consumers.orders-external-events.auto-create-topics}",
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
            final var aEventType = getTypeHeaderValue(record, HeadersConstants.EVENT_TYPE);

            final var aPayload = record.value();

            switch (aEventType) {
                case ShippingCostCalculatedEvent.EVENT_TYPE -> {
                    log.debug("Handling ShippingCostCalculatedEvent");
                    final var aEvent = Json.readValue(aPayload, ShippingCostCalculatedEvent.class);

                    log.debug("Deserialized event and handling: {}", aEvent);
                    this.shippingCostOrderHandler.handle(aEvent);
                    ack.acknowledge();
                    // talvez aqui mostrar algo mais atualizado ou melhor
                    log.info("ShippingCostCalculatedEvent processed {}", aEvent);
                }
                default -> handleMessagingTypeNotSupported(
                        record,
                        aEventType,
                        ORDER_DLT_INVALID,
                        ack,
                        log,
                        HeadersConstants.EVENT_OCCURRED_ON,
                        HeadersConstants.EVENT_TYPE,
                        HeadersConstants.EVENT_ID
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
                HeadersConstants.EVENT_TYPE,
                HeadersConstants.EVENT_ID
        );
    }
}
