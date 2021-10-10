package me.sxmurai.inferno.api.events.network;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SelfConnectionEvent extends Event {
    private final Type type;

    public SelfConnectionEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CONNECT, DISCONNECT
    }
}
