package com.kaua.order.domain.validation.handler;

import com.kaua.order.domain.exceptions.DomainException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationHandler create() {
        return new NotificationHandler(new ArrayList<>());
    }

    public static NotificationHandler create(final Error anError) {
        return new NotificationHandler(new ArrayList<>()).append(anError);
    }

    @Override
    public NotificationHandler append(Error anError) {
        this.errors.add(anError);
        return this;
    }

    @Override
    public NotificationHandler append(ValidationHandler aHandler) {
        this.errors.addAll(aHandler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
