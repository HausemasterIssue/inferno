package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.events.mc.GuiChangeEvent;
import me.sxmurai.inferno.features.settings.Bind;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "InventorySaver", description = "Allows you to interact with inventories after closing them")
public class InventorySaver extends Module {
    public final Setting<Access> access = this.register(new Setting<>("Access", Access.TOGGLED));
    public final Setting<Bind> accessBind = this.register(new Bind("AccessBind", Keyboard.KEY_NONE));

    private GuiContainer gui;

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck() && this.access.getValue() == Access.TOGGLED) {
            mc.displayGuiScreen(this.gui);
        }

        this.gui = null;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onGuiChange(GuiChangeEvent event) {
        if (!Module.fullNullCheck() && event.getCurrentGui() instanceof GuiContainer) {
            System.out.println("penis");
            this.gui = (GuiContainer) event.getCurrentGui();
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        int code = Keyboard.getEventKey();
        if (!Module.fullNullCheck() && !Keyboard.getEventKeyState() && code != Keyboard.KEY_NONE) {
            if (this.access.getValue() == Access.BIND && code == accessBind.getValue().getValue() && this.gui != null) {
                mc.displayGuiScreen(this.gui);
            }
        }
    }

    public enum Access {
        TOGGLED, BIND
    }
}
