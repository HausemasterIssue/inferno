package me.sxmurai.inferno.events.entity;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class UpdateMoveEvent extends Event {
    private final Era era;

    public UpdateMoveEvent(Era era) {
        this.era = era;
    }

    public Era getEra() {
        return era;
    }

    public enum Era {
        PRE, POST
    }
}
