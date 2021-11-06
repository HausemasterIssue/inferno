package me.sxmurai.inferno.impl.event.inferno;

import me.sxmurai.inferno.impl.option.Option;
import net.minecraftforge.fml.common.eventhandler.Event;

public class OptionChangeEvent extends Event {
    private final Option option;

    public OptionChangeEvent(Option option) {
        this.option = option;
    }

    public Option getOption() {
        return option;
    }
}
