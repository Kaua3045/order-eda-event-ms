package com.kaua.order.infrastructure.commands;

import com.kaua.order.domain.commands.InternalCommand;
import com.kaua.order.infrastructure.configurations.json.Json;
import com.kaua.order.infrastructure.constants.HeadersConstants;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaCommandBus implements CommandBus {

    private static final Logger log = LoggerFactory.getLogger(KafkaCommandBus.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaCommandBus(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public <T extends InternalCommand> void dispatch(
            final T aCommand,
            final String aDestination
    ) {
        try {
            log.debug("Dispatching command {} to destination {}", aCommand, aDestination);
            final var aCommandSerialized = Json.writeValueAsString(aCommand);
            final var aProducerRecord = new ProducerRecord<String, Object>(aDestination, aCommandSerialized);
            aProducerRecord.headers().add(HeadersConstants.COMMAND_ID, aCommand.commandId().getBytes());
            aProducerRecord.headers().add(HeadersConstants.COMMAND_TYPE, aCommand.commandType().getBytes());
            aProducerRecord.headers().add(HeadersConstants.COMMAND_OCCURRED_ON, aCommand.occurredOn().toString().getBytes());
            aProducerRecord.headers().add(HeadersConstants.WHO, aCommand.who().getBytes());
            aProducerRecord.headers().add(HeadersConstants.TRACE_ID, aCommand.traceId().getBytes());

            log.debug("Sending command {} to destination {}", aCommand, aDestination);
            this.kafkaTemplate.send(aProducerRecord).get(1, TimeUnit.MINUTES);
            log.info("Command {} dispatched to destination {}", aCommand, aDestination);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            log.error("Error dispatching command {} to destination {}", aCommand, aDestination, e);
//            throw CommandBusException.with(e);
        }
    }
}
