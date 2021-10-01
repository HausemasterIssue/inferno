package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.entity.MoveEvent;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "SafeWalk", description = "Stops you from walking off edges", category = Module.Category.MOVEMENT)
public class SafeWalk extends Module {
    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (mc.player.onGround && isBoundingBoxOffsetEmpty(event.getX(), event.getY(), event.getZ())) {
            event.setX(0.0);
            event.setZ(0.0);
        }
    }

    public static boolean isBoundingBoxOffsetEmpty(double x, double y, double z) {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }
}
