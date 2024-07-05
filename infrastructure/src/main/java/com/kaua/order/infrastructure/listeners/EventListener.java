package com.kaua.order.infrastructure.listeners;

import com.kaua.order.domain.utils.InstantUtils;
import com.kaua.order.infrastructure.exceptions.KafkaHeadersException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class EventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    protected EventListener(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    Instant getOccurredOnHeaderValue(final ConsumerRecord<String, String> message, final String headerName) {
        final var aOccurredOnHeader = getHeader(message, headerName);
        final var aOccurredOnString = new String(aOccurredOnHeader);
        return InstantUtils.fromString(aOccurredOnString).orElse(null);
    }

    String getTypeHeaderValue(final ConsumerRecord<String, String> message, final String headerName) {
        final var aEventTypeHeader = getHeader(message, headerName);
        return new String(aEventTypeHeader);
    }

    String getIdHeaderValue(final ConsumerRecord<String, String> message, final String headerName) {
        final var aEventIdHeader = getHeader(message, headerName);
        return new String(aEventIdHeader);
    }

    ProducerRecord<String, Object> createProducerRecordWithHeaders(final ConsumerRecord<String, String> message, final String topic) {
        final var aProducerRecord = new ProducerRecord<String, Object>(topic, message.value());
        message.headers().forEach(aProducerRecord.headers()::add);
        return aProducerRecord;
    }

    void handlePublishMessageTimeout(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Exception ex,
            final Logger log
    ) {
        log.error("Error on sending message to topic {} and nack message {}", topic, message, ex);
        ack.nack(Duration.ofSeconds(5));
    }

    void handlePublishMessageToRetryTopic(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log,
            final String typeHeader,
            final String idHeader
    ) {
        try {
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();
            final var aMessageType = getTypeHeaderValue(message, typeHeader);
            final var aMessageId = getIdHeaderValue(message, idHeader);
            log.warn("Message [type:{}] [id:{}] sent to retry topic: {}, payload: {}",
                    aMessageType,
                    aMessageId,
                    topic,
                    message.value()
            );
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingNotContainsHeaders(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log,
            final String errorMessage
    ) {
        message.headers().add("error_message", errorMessage.getBytes());
        try {
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();
            log.warn("Event sent to DLT topic: {}, because: {}, payload: {}",
                    topic,
                    errorMessage,
                    message.value()
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingErrorAndSendToDlt(
            final ConsumerRecord<String, String> message,
            final String topic,
            final Acknowledgment ack,
            final Logger log,
            final String errorMessage,
            final String typeHeader,
            final String idHeader
    ) {
        try {
            message.headers().add("error_message", errorMessage.getBytes());
            kafkaTemplate.send(createProducerRecordWithHeaders(message, topic)).get(1, TimeUnit.MINUTES);
            ack.acknowledge();

            final var aMessageType = getTypeHeaderValue(message, typeHeader);
            final var aMessageId = getIdHeaderValue(message, idHeader);
            log.warn("Message [type:{}] [id:{}] sent to DLT topic: {}, because: {}, payload: {}",
                    aMessageType,
                    aMessageId,
                    topic,
                    errorMessage,
                    message.value()
            );
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            handlePublishMessageTimeout(message, topic, ack, e, log);
        }
    }

    void handleMessagingTypeNotSupported(
            final ConsumerRecord<String, String> message,
            final String messageType,
            final String dltTopic,
            final Acknowledgment ack,
            final Logger log,
            final String occurredOnHeader,
            final String typeHeader,
            final String idHeader
    ) {
        final var aOccurredOn = getOccurredOnHeaderValue(message, occurredOnHeader);
        final var aDuration = Duration.between(aOccurredOn, InstantUtils.now());

        if (aDuration.toDays() >= 7) {
            log.warn("Message with type {} not supported after {} days, sent to DLT: {}", messageType, aDuration.toDays(), dltTopic);
            handleMessagingErrorAndSendToDlt(
                    message,
                    dltTopic,
                    ack,
                    log,
                    "Message type not supported after " + aDuration.toDays() + " days, type: " + messageType,
                    typeHeader,
                    idHeader
            );
        } else {
            log.warn("Message type not supported: {}", messageType);
            ack.nack(Duration.ofSeconds(5));
        }
    }

    private byte[] getHeader(final ConsumerRecord<String, String> message, final String headerName) {
        return Optional.ofNullable(message.headers().lastHeader(headerName))
                .map(Header::value)
                .orElseThrow(() -> KafkaHeadersException.with("%s header not found".formatted(headerName)));
    }
}
