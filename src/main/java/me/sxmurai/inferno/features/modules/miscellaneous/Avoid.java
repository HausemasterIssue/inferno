package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Avoid", description = "Avoids stuff")
public class Avoid extends Module {
    public static Avoid INSTANCE;

    public final Setting<Boolean> cactus = new Setting<>("Cactus", false);
    public final Setting<Boolean> fire = new Setting<>("Fire", false);
    public final Setting<Boolean> unloaded = new Setting<>("Unloaded", false); // 100% not an idea from future

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
