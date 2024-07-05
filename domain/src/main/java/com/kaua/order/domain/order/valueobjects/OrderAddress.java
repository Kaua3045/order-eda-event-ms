package com.kaua.order.domain.order.valueobjects;

import com.kaua.order.domain.ValueObject;
import com.kaua.order.domain.exceptions.NotificationException;
import com.kaua.order.domain.validation.Error;
import com.kaua.order.domain.validation.handler.NotificationHandler;

import java.util.Optional;

public class OrderAddress extends ValueObject {

    private final String street;
    private final String number;
    private final String complement;
    private final String city;
    private final String state;
    private final String zipCode;

    private OrderAddress(
            final String street,
            final String number,
            final String complement,
            final String city,
            final String state,
            final String zipCode
    ) {
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        validate();
    }

    public static OrderAddress create(
            final String street,
            final String number,
            final String complement,
            final String city,
            final String state,
            final String zipCode
    ) {
        return new OrderAddress(street, number, complement, city, state, zipCode);
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public Optional<String> getComplement() {
        return Optional.ofNullable(complement);
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    private void validate() {
        final var aNotificationHandler = NotificationHandler.create();

        if (this.street == null || this.street.isBlank()) {
            aNotificationHandler.append(new Error("'street' should not be null or empty"));
        }
        if (this.number == null || this.number.isBlank()) {
            aNotificationHandler.append(new Error("'number' should not be null or empty"));
        }
        if (getComplement().isPresent() && getComplement().get().isEmpty()) {
            aNotificationHandler.append(new Error("'complement' should not be empty"));
        }
        if (this.city == null || this.city.isBlank()) {
            aNotificationHandler.append(new Error("'city' should not be null or empty"));
        }
        if (this.state == null || this.state.isBlank()) {
            aNotificationHandler.append(new Error("'state' should not be null or empty"));
        }
        if (this.zipCode == null || this.zipCode.isBlank()) {
            aNotificationHandler.append(new Error("'zipCode' should not be null or empty"));
        }

        if (aNotificationHandler.hasError()) {
            throw new NotificationException("Failed to create OrderAddress", aNotificationHandler);
        }
    }

    @Override
    public String toString() {
        return "OrderAddress(" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", complement='" + complement + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ')';
    }
}
