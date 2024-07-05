package com.kaua.order.infrastructure.utils;

import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.validation.Error;

import java.util.Collections;
import java.util.List;

public record ApiError(String message, List<Error> errors) {

    public static ApiError from(final DomainException exception) {
        return new ApiError(exception.getMessage(), exception.getErrors());
    }

    public static ApiError from(final String message) {
        return new ApiError(message, Collections.emptyList());
    }
}
