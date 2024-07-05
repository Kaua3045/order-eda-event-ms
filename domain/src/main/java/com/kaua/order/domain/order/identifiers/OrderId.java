package com.kaua.order.domain.order.identifiers;

import com.kaua.order.domain.Identifier;
import com.kaua.order.domain.utils.IdUtils;

import java.util.Objects;

public class OrderId extends Identifier {

    private final String value;

    private OrderId(final String value) {
        this.value = Objects.requireNonNull(value, "'id' should not be null");
    }

    public static OrderId from(final String value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(IdUtils.generateIdWithoutHyphen());
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final OrderId orderId = (OrderId) o;
        return Objects.equals(getValue(), orderId.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }
}
