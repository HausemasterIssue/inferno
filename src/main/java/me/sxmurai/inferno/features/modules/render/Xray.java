package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Xray", description = "Lets you see through shit", category = Module.Category.RENDER)
public class Xray extends Module {
    public static Xray INSTANCE;

    // @todo i legit have no idea wtf to do here
    public final Setting<Integer> opacity = this.register(new Setting<>("Opacity", 125, 0, 255));

    private boolean shouldReload = false;

    public Xray() {
        INSTANCE = this;
    }

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            shouldReload = true;
        } else {
            mc.renderGlobal.loadRenderers();
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.shouldReload) {
            this.shouldReload = false;
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.renderGlobal.loadRenderers();
        }
    }
}
