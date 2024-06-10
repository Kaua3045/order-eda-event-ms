package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.AggregateRoot;
import com.kaua.order.domain.Identifier;
import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.validation.ValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NotFoundExceptionTest extends UnitTest {

    @Test
    void givenAValidAggregate_whenCallNotFoundExceptionWith_ThenReturnNotFoundException() {
        // given
        final var aggregate = SampleAggregate.class;
        final var aId = "123";
        final var expectedErrorMessage = "SampleAggregate with id 123 was not found";

        // when
        final var notFoundException = NotFoundException.with(aggregate, aId);
        // then
        Assertions.assertEquals(expectedErrorMessage, notFoundException.get().getMessage());
    }

    static class SampleAggregate extends AggregateRoot<SampleIdentifier> {
        public SampleAggregate(SampleIdentifier id) {
            super(id);
        }

        @Override
        public void validate(ValidationHandler aHandler) {

        }
    }

    static class SampleIdentifier extends Identifier {
        private final String value;

        public SampleIdentifier(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
