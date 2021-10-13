package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.events.entity.JumpEvent;
import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.misc.HoleManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Surround", description = "Surrounds your lower hitbox with obsidian", category = Module.Category.COMBAT)
public class Surround extends Module {
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> packet = new Value<>("Packet", false);
    public final Value<Boolean> sneak = new Value<>("Sneak", false);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Boolean> silent = new Value<>("Silent", false);
    public final Value<Boolean> center = new Value<>("Center", false);
    public final Value<Boolean> helpers = new Value<>("Helpers", true);
    public final Value<Integer> blocks = new Value<>("Blocks", 1, 1, 5);
    public final Value<Boolean> feet = new Value<>("Feet", true);
    public final Value<Disable> disable = new Value<>("Disable", Disable.FINISHED);

    private int placed = 0;
    private final Queue<BlockPos> positions = new ConcurrentLinkedQueue<>();

    private int oldSlot = -1;
    private EnumHand hand = EnumHand.MAIN_HAND;

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            if (this.hand != EnumHand.OFF_HAND && this.oldSlot != -1) {
                InventoryUtils.switchTo(this.oldSlot, this.silent.getValue());
            }

            this.oldSlot = -1;
            this.hand = EnumHand.MAIN_HAND;
        }

        this.positions.clear();
        this.placed = 0;
    }

    @SubscribeEvent
    public void onJump(JumpEvent event) {
        if (event.getPlayer() == mc.player && this.disable.getValue() == Disable.JUMP) {
            this.toggle();
        }
    }

    @Override
    public void onTick() {
        if (this.placed >= this.blocks.getValue()) {
            this.placed = 0;
        }

        if ((this.oldSlot == -1 || this.hand != EnumHand.OFF_HAND) && !InventoryUtils.isHolding(Blocks.OBSIDIAN, true)) {
            int slot = InventoryUtils.getHotbarBlockSlot(Blocks.OBSIDIAN, true);
            if (slot == -1) {
                return;
            }

            if (slot == 45) {
                this.hand = EnumHand.OFF_HAND;
            } else {
                this.hand = EnumHand.MAIN_HAND;
                this.oldSlot = mc.player.inventory.currentItem;
                InventoryUtils.switchTo(slot, this.silent.getValue());
            }

            mc.player.setActiveHand(this.hand);
        }

        if (this.positions.isEmpty()) {
            BlockPos base = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            for (BlockPos surroundPosition : HoleManager.SURROUND_POSITIONS) {
                if (!this.feet.getValue() && surroundPosition.equals(new BlockPos(0.0, -1.0, 0.0))) {
                    continue;
                }

                this.positions.add(base.add(surroundPosition));
            }

            this.positions.removeIf((pos) -> !mc.world.isAirBlock(pos));

            if (this.positions.isEmpty()) {
                if (this.disable.getValue() == Disable.FINISHED) {
                    this.toggle();
                } else if (this.disable.getValue() == Disable.SNEAK && mc.player.isSneaking()) {
                    this.toggle();
                }
            }
        } else {
            if (this.center.getValue()) {
                BlockPos centered = new BlockPos(Math.floor(mc.player.posX) + 0.5, mc.player.posY, Math.floor(mc.player.posZ) + 0.5);
                if (Math.abs(centered.x - mc.player.posX) <= 0.1 || Math.abs(centered.z - mc.player.posZ) <= 0.1) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(centered.x, centered.y, centered.z, mc.player.onGround));
                    mc.player.setPosition(centered.x, centered.y, centered.z);
                }
            }

            while (!this.positions.isEmpty()) {
                if (this.placed >= this.blocks.getValue()) {
                    break;
                }

                BlockPos pos = this.positions.poll();
                if (pos == null) {
                    continue;
                }

                BlockUtil.place(pos, this.hand, this.swing.getValue(), this.sneak.getValue(), this.packet.getValue(), this.rotate.getValue());

                if (mc.world.isAirBlock(pos) && this.helpers.getValue()) {
                    this.positions.add(pos.add(0.0, -1.0, 0.0)); // @todo loop through directions and find best facing
                    this.positions.add(pos);
                }

                ++this.placed;
            }
        }
    }

    public enum Disable {
        MANUAL, FINISHED, SNEAK, JUMP
    }
}
