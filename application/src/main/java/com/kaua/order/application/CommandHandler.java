package com.kaua.order.application;

import com.kaua.order.domain.commands.InternalCommand;

public abstract class CommandHandler<T extends InternalCommand> {

    public abstract void handle(final T aCommand);
}
