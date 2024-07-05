package com.kaua.order.domain.exceptions;

import com.kaua.order.domain.UnitTest;
import com.kaua.order.domain.validation.Error;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DomainExceptionTest extends UnitTest {

    @Test
    void givenAValidListOfError_whenCallDomainExceptionWith_ThenReturnDomainException() {
        var errors = List.of(new Error("Common Error"));

        var domainException = DomainException.with(errors);

        Assertions.assertEquals(errors, domainException.getErrors());
    }

    @Test
    void givenAValidError_whenCallDomainExceptionWith_ThenReturnDomainException() {
        var error = new Error("Common Error");

        var domainException = DomainException.with(error);

        Assertions.assertEquals(List.of(error), domainException.getErrors());
    }

    @Test
    void givenAValidListOfErrorAndMessage_whenCallNewDomainException_ThenReturnDomainException() {
        var errors = List.of(new Error("Common Error"));

        var domainException = new DomainException("Common Error", errors);

        Assertions.assertEquals(errors, domainException.getErrors());
    }
}
