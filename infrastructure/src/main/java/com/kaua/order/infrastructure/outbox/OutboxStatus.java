package com.kaua.order.infrastructure.outbox;

public enum OutboxStatus {

    PENDING,
    FAILED,
    PROCESSED
}
