package com.kaua.order.infrastructure.api.controllers;

import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.infrastructure.exceptions.EventStoreException;
import com.kaua.order.infrastructure.utils.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomainException(final DomainException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ApiError.from(ex));
    }

    @ExceptionHandler(EventStoreException.class)
    public ResponseEntity<ApiError> handleEventStoreException(final EventStoreException ex) {
        log.error("Internal server error, in event store", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.from("Internal server error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(final Exception ex) {
        log.error("Internal server error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiError.from("Internal server error"));
    }
}
