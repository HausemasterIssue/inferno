package me.sxmurai.inferno.events.mc;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class GuiChangeEvent extends Event {
    private final GuiScreen oldGui;
    private final GuiScreen currentGui;

    public GuiChangeEvent(GuiScreen oldGui, GuiScreen currentGui) {
        this.oldGui = oldGui;
        this.currentGui = currentGui;
    }

    public GuiScreen getOldGui() {
        return oldGui;
    }

    public GuiScreen getCurrentGui() {
        return currentGui;
    }
}
