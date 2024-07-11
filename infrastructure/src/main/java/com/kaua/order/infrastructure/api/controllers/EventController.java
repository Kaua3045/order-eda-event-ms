package com.kaua.order.infrastructure.api.controllers;

import com.kaua.order.infrastructure.api.EventAPI;
import com.kaua.order.infrastructure.models.EventRequest;
import com.kaua.order.infrastructure.utils.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
public class EventController implements EventAPI {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventController(final KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public ResponseEntity<?> createEvent(final EventRequest request) {
        log.debug("Received a request to create an event: {}", request);

        try {
            this.kafkaTemplate.send(request.toProducerRecord()).get(1, TimeUnit.MINUTES);
            log.info("Simulated external event created successfully");
        } catch (Exception ex) {
            log.error("Error creating event: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiError.from("Internal server error"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
