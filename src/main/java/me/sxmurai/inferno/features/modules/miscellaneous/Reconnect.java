package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Reconnect", description = "Reconnects you to the previous server")
public class Reconnect extends Module {
    public static Reconnect INSTANCE;

    public final Setting<Float> delay = new Setting<>("Delay", 5.0f, 0.0f, 20.0f);

    public ServerData serverData = null;

    public Reconnect() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (mc.currentServerData != null) {
            this.serverData = mc.currentServerData;
        }
    }
}
