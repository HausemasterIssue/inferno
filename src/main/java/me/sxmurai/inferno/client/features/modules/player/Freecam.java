package me.sxmurai.inferno.client.features.modules.player;

import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.utils.RotationUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "Freecam", description = "Moves around out of your main player", category = Module.Category.PLAYER)
public class Freecam extends Module {
    public final Value<Boolean> copy = new Value<>("Copy", true);
    public final Value<Float> speed = new Value<>("Speed", 2.0f, 1.0f, 10.0f);

    private EntityOtherPlayerMP fake = null;
    private Vec3d oldPos = null;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            fake = null;
            this.toggle();
        }

        this.spawn(false);
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
            mc.player.noClip = false;
            mc.player.capabilities.isFlying = false;
            mc.player.setPosition(this.oldPos.x, this.oldPos.y, this.oldPos.z);

            this.spawn(true);
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate() {
        mc.player.capabilities.isFlying = true;
        mc.player.noClip = true;

        if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
            RotationUtils.Rotation yes = RotationUtils.getDirectionalSpeed(this.speed.getValue() / 10.0);
            mc.player.motionX = yes.getYaw();
            mc.player.motionZ = yes.getPitch();
        } else {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode)) {
            mc.player.motionY -= (this.speed.getValue() / 10.0);
        } else if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode)) {
            mc.player.motionY += (this.speed.getValue() / 10.0);
        }
    }

    private void spawn(boolean onlyRemove) {
        if (fake != null) {
            mc.world.removeEntity(fake);
            mc.world.removeEntityDangerously(fake);
            fake = null;
        }

        if (onlyRemove) {
            return;
        }

        oldPos = mc.player.getPositionVector();
        fake = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        fake.copyLocationAndAnglesFrom(mc.player);
        if (this.copy.getValue()) {
            fake.inventory.copyInventory(mc.player.inventory);
        }

        fake.setEntityId(-694201338);
        mc.world.spawnEntity(fake);
    }
}
