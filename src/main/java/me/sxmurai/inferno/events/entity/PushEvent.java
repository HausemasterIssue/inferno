package me.sxmurai.inferno.events.entity;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PushEvent extends Event {
    private final Type type;

    public PushEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        BLOCKS, ENTITY, LIQUID
    }
}
