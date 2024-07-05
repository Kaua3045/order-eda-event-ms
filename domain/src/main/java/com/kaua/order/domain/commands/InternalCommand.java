package com.kaua.order.domain.commands;

import java.time.Instant;

public interface InternalCommand {

    String commandId();

    String commandType();

    Instant occurredOn();

    String who();

    String traceId();
}
