package me.sxmurai.inferno.client.modules.miscellaneous;

import me.sxmurai.inferno.api.events.entity.MoveEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Avoid", description = "Avoids stuff")
public class Avoid extends Module {
    public static Avoid INSTANCE;

    public final Value<Boolean> cactus = new Value<>("Cactus", false);
    public final Value<Boolean> fire = new Value<>("Fire", false);
    public final Value<Boolean> unloaded = new Value<>("Unloaded", false); // 100% not an idea from future

    public Avoid() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (this.unloaded.getValue()) {
            for (EnumFacing facing : EnumFacing.values()) {
                if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                    continue;
                }

                if (!mc.world.isBlockLoaded(mc.player.getPosition().offset(facing), true)) {
                    event.setX(0.0);
                    event.setZ(0.0);
                    break;
                }
            }
        }
    }
}
