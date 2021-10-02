package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.block.BlockWeb;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "NoFall", description = "Tries to negate fall damage", category = Module.Category.PLAYER)
public class NoFall extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET);
    public final Setting<Boolean> packetLook = new Setting<>("PacketLook", false, (v) -> mode.getValue() != Mode.PACKET);
    public final Setting<Float> distance = new Setting<>("Distance", 3.0f, 1.0f, 100.0f);

    private int oldSlot = -1;
    private EnumHand hand;
    private float oldPitch = -1.0f;

    @Override
    protected void onDeactivated() {
        this.oldSlot = -1;
        this.hand = null;
        this.oldPitch = -1.0f;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.fallDistance > this.distance.getValue()) {
            if (this.mode.getValue() == Mode.PACKET) {
                mc.player.connection.sendPacket(new CPacketPlayer(true));
            } else if (this.mode.getValue() == Mode.WATER_BUCKET || this.mode.getValue() == Mode.COBWEB) {
                int slot = this.mode.getValue() == Mode.WATER_BUCKET ?
                        InventoryUtils.getHotbarItemSlot(Items.WATER_BUCKET, true) :
                        InventoryUtils.getHotbarBlockSlot(BlockWeb.class, true);

                // rip to you
                if (slot == -1) {
                    return;
                }

                if (slot == 45) {
                    this.hand = EnumHand.OFF_HAND;
                } else {
                    this.hand = EnumHand.MAIN_HAND;
                    this.oldSlot = mc.player.inventory.currentItem;

                    InventoryUtils.switchTo(slot, false);

                    this.doWizardShit();
                }
            }
        }

        if (mc.player.onGround && (this.mode.getValue() == Mode.WATER_BUCKET || this.mode.getValue() == Mode.COBWEB)) {
            if (this.oldPitch != -1.0f && !this.packetLook.getValue()) {
                mc.player.rotationPitch = this.oldPitch;
                this.oldPitch = -1.0f;
            }

            if (this.oldSlot != -1) {
                InventoryUtils.switchTo(this.oldSlot, false);
                this.oldSlot = -1;
                this.hand = null;
            }
        }
    }

    private void doWizardShit() {
        if (this.oldPitch == -1.0f) {
            this.oldPitch = mc.player.rotationPitch;
        }

        if (packetLook.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90.0f, false));
        } else {
            mc.player.rotationPitch = 90.0f;
        }

        mc.playerController.processRightClick(mc.player, mc.world, this.hand);
    }

    public enum Mode {
        PACKET, WATER_BUCKET, COBWEB
    }
}
