package me.sxmurai.inferno.api.events.render;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEvent extends Event {
    private final Type type;

    public RenderEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        WORLD, HUD
    }
}
