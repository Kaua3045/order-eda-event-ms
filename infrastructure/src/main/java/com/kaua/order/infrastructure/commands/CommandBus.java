package com.kaua.order.infrastructure.commands;

import com.kaua.order.domain.commands.InternalCommand;

public interface CommandBus {

    <T extends InternalCommand> void dispatch(T aCommand, String aDestination);
}
