package com.kaua.order.infrastructure.transaction;

import java.util.function.Supplier;

public interface TransactionManager {

    <T> TransactionResult<T> execute(Supplier<T> action);
}
