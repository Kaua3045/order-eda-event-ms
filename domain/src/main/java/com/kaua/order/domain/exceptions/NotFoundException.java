package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.validation.Error;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NotFoundException extends DomainException {

    protected NotFoundException(final String aMessage, final List<Error> aErrors) {
        super(aMessage, aErrors);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String id
    ) {
        final var aError = "%s with id %s was not found".formatted(anAggregate.getSimpleName(), id);

        return () -> new NotFoundException(aError, Collections.emptyList());
    }

    public static Supplier<NotFoundException> with(
            final String anAggregate,
            final String id
    ) {
        final var aError = "%s with id %s was not found".formatted(anAggregate, id);

        return () -> new NotFoundException(aError, Collections.emptyList());
    }
}
