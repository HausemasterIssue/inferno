package me.sxmurai.inferno.api.entity;

import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateWalkingPlayerEvent extends Event {
    private final Era era;

    public UpdateWalkingPlayerEvent(Era era) {
        this.era = era;
    }

    public Era getEra() {
        return era;
    }

    public enum Era {
        PRE, POST
    }
}
