package me.sxmurai.inferno.api.util;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.impl.features.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockUtil implements Wrapper {
    public static void place(BlockPos pos, EnumHand hand, boolean packet, boolean sneak, boolean swing, boolean rotate) {
        EnumFacing facing = BlockUtil.getFacing(pos);
        if (facing == null) {
            return;
        }

        BlockPos neighbor = pos.offset(facing);

        if (sneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        if (rotate) {
            Inferno.rotationManager.look(neighbor);
        }

        Vec3d hitVec = new Vec3d(neighbor.x + 0.5, neighbor.y + 0.5, neighbor.z + 0.5).add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.5));

        if (packet) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbor, facing.getOpposite(), hand, (float) (hitVec.x - pos.x), (float) (hitVec.y - pos.y), (float) (hitVec.z - pos.z)));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, facing.getOpposite(), hitVec, hand);
        }

        if (swing) {
            mc.player.swingArm(hand);
        }

        if (sneak) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facing);
            if (mc.world.isAirBlock(neighbor) || BlockUtil.intersects(neighbor)) {
                continue;
            }

            return facing;
        }

        return null;
    }

    public static boolean intersects(BlockPos pos) {
        return !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos), (v) -> v != null && !v.isDead).isEmpty();
    }
}
