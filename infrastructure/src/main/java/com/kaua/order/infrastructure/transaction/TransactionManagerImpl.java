package com.kaua.order.infrastructure.transaction;

import com.kaua.order.domain.validation.Error;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class TransactionManagerImpl implements TransactionManager {

    private final TransactionTemplate aTransactionTemplate;

    public TransactionManagerImpl(TransactionTemplate aTransactionTemplate) {
        this.aTransactionTemplate = aTransactionTemplate;
    }

    @Override
    public <T> TransactionResult<T> execute(Supplier<T> action) {
        try {
            T result = aTransactionTemplate.execute(status -> {
                try {
                    return action.get();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
            return TransactionResult.success(result);
        } catch (Exception e) {
            return TransactionResult.failure(new Error(e.getMessage()));
        }
    }
}
