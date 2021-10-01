package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.BlockUtil;
import me.sxmurai.inferno.utils.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "Trap", description = "Traps another player, mainly shitter holecampers", category = Module.Category.COMBAT)
public class Trap extends Module {
    public final Setting<Float> targetRange = this.register(new Setting<>("TargetRange", 4.5f, 1.0f, 6.0f));
    public final Setting<Boolean> noFriends = this.register(new Setting<>("NoFriends", false));

    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", false));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", true));
    public final Setting<Integer> blocksPerTick = this.register(new Setting<>("BlocksPerTick", 1, 1, 5));
    public final Setting<Boolean> helpers = this.register(new Setting<>("Helpers", true));

    private EntityPlayer target = null;
    private final Queue<BlockPos> placements = new ConcurrentLinkedQueue<>();
    private int placed = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        if (this.placements.isEmpty()) {
            if (this.target == null || this.target.isDead || mc.player.getDistance(this.target) > this.targetRange.getValue()) {
                this.target = EntityUtils.getClosest(this.target, this.targetRange.getValue(), this.noFriends.getValue());
                if (this.target == null) {
                    return;
                }
            }

            BlockPos base = new BlockPos(this.target.posX, this.target.posY, this.target.posZ);
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos neighbor = base.offset(facing);

                this.placements.add(neighbor);
                this.placements.add(neighbor.add(0.0, 1.0, 0.0));
            }

            BlockPos top = base.add(0.0, 3.0, 0.0);

            if (this.helpers.getValue()) {
                this.placements.add(top.offset(EnumFacing.SOUTH));
            }

            this.placements.add(top);
        } else {
            while (!this.placements.isEmpty()) {
                BlockPos pos = this.placements.poll();
                if (pos == null) {
                    break;
                }

                BlockUtil.place(pos, EnumHand.MAIN_HAND, this.swing.getValue(), this.sneak.getValue(), this.packet.getValue(), this.rotate.getValue());

                ++this.placed;
                if (this.placed >= this.blocksPerTick.getValue()) {
                    break;
                }
            }
        }
    }
}
