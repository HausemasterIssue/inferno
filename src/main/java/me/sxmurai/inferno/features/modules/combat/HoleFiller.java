package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.HoleManager;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.BlockUtil;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.RotationUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

@Module.Define(name = "HoleFiller", description = "(cringe) Fills in safe holes", category = Module.Category.COMBAT)
public class HoleFiller extends Module {
    public final Setting<Float> range = this.register(new Setting<>("Range", 4.0f, 1.0f, 6.5f));
    public final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 1, 1, 6));
    public final Setting<Boolean> silent = this.register(new Setting<>("Silent", false));
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", false));
    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", false));

    private final ArrayList<BlockPos> blocks = new ArrayList<>();
    private int oldSlot = -1;

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck() && this.oldSlot != -1) {
            InventoryUtils.switchTo(this.oldSlot, this.silent.getValue());
        }

        this.oldSlot = -1;
        this.blocks.clear();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        int slot = InventoryUtils.getHotbarBlockSlot(Blocks.OBSIDIAN, false);
        if (slot == -1) {
            this.toggle();
            return;
        }

        if (this.oldSlot == -1) {
            this.oldSlot = mc.player.inventory.currentItem;
            InventoryUtils.switchTo(slot, this.silent.getValue());
        }

        int b = 0;

        if (!this.blocks.isEmpty()) {
            for (int i = 0; i < this.blocks.size(); ++i) {
                BlockPos pos = this.blocks.get(i);
                if (b >= this.blocksPerTick.getValue()) {
                    continue;
                }

                this.blocks.remove(pos);

                if (BlockUtil.intersectsWith(pos) || BlockUtil.getBlockFromPos(pos) != Blocks.AIR) {
                    continue;
                }

                this.place(pos);
                ++b;
            }
        } else {
            for (HoleManager.Hole hole : Inferno.holeManager.getHoles()) {
                BlockPos pos = hole.getPos();
                if (mc.player.getDistance(pos.x, pos.y, pos.z) > this.range.getValue()) {
                    continue;
                }

                if (BlockUtil.intersectsWith(pos) || BlockUtil.getBlockFromPos(pos) != Blocks.AIR) {
                    continue;
                }

                if (b >= this.blocksPerTick.getValue() && !this.blocks.contains(pos)) {
                    this.blocks.add(pos);
                    continue;
                }

                this.place(pos);
                ++b;
            }
        }
    }

    private void place(BlockPos pos) {
        if (this.rotate.getValue()) {
            RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.x + 0.5, pos.y, pos.z + 0.5));
            Inferno.rotationManager.setRotations(rotation.getYaw(), rotation.getPitch());
        }

        BlockUtil.place(pos, EnumHand.MAIN_HAND, this.swing.getValue(), this.sneak.getValue(), this.packet.getValue(), false);
    }
}
