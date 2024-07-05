package com.kaua.order.infrastructure.transaction;

import com.kaua.order.domain.validation.Error;

public class TransactionResult<T> {

    private final T success;
    private final Error error;

    private TransactionResult(T success, Error error) {
        this.success = success;
        this.error = error;
    }

    public static <T> TransactionResult<T> success(T aSuccessResult) {
        return new TransactionResult<>(aSuccessResult, null);
    }

    public static <T> TransactionResult<T> failure(Error aErrorResult) {
        return new TransactionResult<>(null, aErrorResult);
    }

    public boolean isFailure() {
        return this.error != null;
    }

    public T getSuccessResult() {
        return this.success;
    }

    public Error getErrorResult() {
        return this.error;
    }
}
