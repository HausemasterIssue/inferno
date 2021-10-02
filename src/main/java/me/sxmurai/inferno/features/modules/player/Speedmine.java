package me.sxmurai.inferno.features.modules.player;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.events.world.BlockDestroyEvent;
import me.sxmurai.inferno.events.world.BlockHitEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Speedmine", description = "Goes vroom vroom when fucking someone", category = Module.Category.PLAYER)
public class Speedmine extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.PACKET);
    public final Setting<Float> distance = new Setting<>("Distance", 4.5f, 1.0f, 10.0f);
    public final Setting<Boolean> reset = new Setting<>("Reset", false);
    public final Setting<Boolean> swing = new Setting<>("Swing", true);
    public final Setting<Boolean> doublePacket = new Setting<>("Double", false);
    public final Setting<Boolean> pickaxe = new Setting<>("Pickaxe", true);
    public final Setting<Boolean> switchTo = new Setting<>("AutoSwitch", false, (v) -> pickaxe.getValue());
    public final Setting<Boolean> silent = new Setting<>("Silent", false, (v) -> pickaxe.getValue() && switchTo.getValue());
    public final Setting<Boolean> render = new Setting<>("Render", true);

    private int oldSlot = -1;
    private BlockPos currentPos = null;
    private final Timer timer = new Timer();

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            this.doSwitchBack();
        }

        this.oldSlot = -1;
        this.currentPos = null;
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.currentPos != null && mc.player.getDistance(this.currentPos.x, this.currentPos.y, this.currentPos.z) > this.distance.getValue()) {
            this.currentPos = null;
            this.doSwitchBack();
        }

        if (this.currentPos != null && mc.world.getBlockState(this.currentPos).getMaterial().isReplaceable()) {
            this.currentPos = null;
            this.doSwitchBack();
        }
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck() && this.currentPos != null && this.render.getValue()) {
            boolean passed = this.timer.passedMs(2000L);
            RenderUtils.drawFilledBox(new AxisAlignedBB(this.currentPos).offset(RenderUtils.screen()), ColorUtils.toRGBA(passed ? 0 : 255, passed ? 255 : 0, 0, 80));
        }
    }

    @SubscribeEvent
    public void onBlockHit(BlockHitEvent event) {
        mc.playerController.isHittingBlock = this.reset.getValue();

        if (this.pickaxe.getValue() && !InventoryUtils.isHolding(ItemPickaxe.class, false)) {
            if (!this.switchTo.getValue()) {
                return;
            }

            int slot = InventoryUtils.getHotbarItemSlot(ItemPickaxe.class, false);
            if (slot == -1) {
                return;
            }

            this.oldSlot = mc.player.inventory.currentItem;
            InventoryUtils.switchTo(slot, this.silent.getValue());
        }

        this.currentPos = event.getPos();

        if (this.swing.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }

        switch (this.mode.getValue()) {
            case PACKET: {
                this.timer.reset();
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                break;
            }

            case DAMAGE: {
                mc.playerController.curBlockDamageMP = 1.0f;
                break;
            }

            case INSTANT: {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                mc.playerController.onPlayerDestroyBlock(event.getPos());
                mc.world.setBlockToAir(event.getPos());
                break;
            }
        }

        if (this.doublePacket.getValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            mc.playerController.onPlayerDestroyBlock(event.getPos());
            mc.world.setBlockToAir(event.getPos());
        }
    }

    @SubscribeEvent
    public void onDestroyBlock(BlockDestroyEvent event) {
        if (this.currentPos == event.getPos()) {
            this.currentPos = null;
            this.doSwitchBack();
        }
    }

    private void doSwitchBack() {
        if (this.pickaxe.getValue() && this.switchTo.getValue() && this.oldSlot != -1 && InventoryUtils.isHolding(ItemPickaxe.class, false)) {
            InventoryUtils.switchTo(this.oldSlot, this.silent.getValue());
            this.oldSlot = -1;
        }
    }

    public enum Mode {
        PACKET, INSTANT, DAMAGE
    }
}
