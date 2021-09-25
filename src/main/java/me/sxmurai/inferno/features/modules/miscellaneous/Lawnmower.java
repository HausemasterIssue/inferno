package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.BlockUtil;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

@Module.Define(name = "Lawnmower", description = "It does what you think it does lmao")
public class Lawnmower extends Module {
    public final Setting<Float> range = this.register(new Setting<>("Range", 5.0f, 1.0f, 8.0f));
    public final Setting<Integer> delay = this.register(new Setting<>("Delay", 1, 0, 10));
    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Boolean> shears = this.register(new Setting<>("Shears", false));
    public final Setting<Boolean> silent = this.register(new Setting<>("Silent", false, (v) -> shears.getValue()));
    public final Setting<Boolean> flowers = this.register(new Setting<>("Flowers", true));

    private final ArrayList<BlockPos> blocks = new ArrayList<>();
    private final TickTimer timer = new TickTimer();

    @Override
    protected void onDeactivated() {
        this.blocks.clear();
        this.timer.reset();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        this.timer.tick();
        this.blocks.removeIf((pos) -> mc.player.getDistance(pos.x, pos.y, pos.z) > this.range.getValue());

        if (this.blocks.isEmpty()) {
            List<BlockPos> b = BlockUtil.getSphere(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), this.range.getValue().intValue(), this.range.getValue().intValue(), false, true, 0);
            if (!b.isEmpty()) {
                for (BlockPos pos : b) {
                    Block block = mc.world.getBlockState(pos).getBlock();
                    if (!(block instanceof BlockTallGrass || block instanceof BlockDoublePlant || (this.flowers.getValue() && block instanceof BlockFlower))) {
                        continue;
                    }

                    if (!this.blocks.contains(pos)) {
                        this.blocks.add(pos);
                    }
                }
            }
        } else {
            if (this.shears.getValue()) {
                int slot = InventoryUtils.getHotbarItemSlot(Items.SHEARS, false);
                if (slot != -1) {
                    InventoryUtils.switchTo(slot, this.silent.getValue());
                }
            }

            if (this.timer.passed(this.delay.getValue())) {
                this.timer.reset();

                for (int i = 0; i < this.blocks.size(); ++i) {
                    BlockPos pos = this.blocks.get(i);
                    if (mc.world.isAirBlock(pos)) {
                        this.blocks.remove(pos);
                        continue;
                    }

                    if (this.rotate.getValue()) {
                        RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5));
                        Inferno.rotationManager.setRotations(rotation.getYaw(), rotation.getPitch());
                    }

                    mc.playerController.onPlayerDamageBlock(pos, BlockUtil.getFacing(pos));

                    if (this.swing.getValue()) {
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }

                    this.blocks.remove(pos);
                }
            }
        }
    }
}
