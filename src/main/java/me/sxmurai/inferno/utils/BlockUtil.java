package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockUtil extends Feature {
    public static ArrayList<BlockPos> getSphere(BlockPos pos, int radius, int height, boolean hollow, boolean sphere, int yOffset) {
        final ArrayList<BlockPos> blocks = new ArrayList<>();
        float cx = pos.getX(), cy = pos.getY(), cz = pos.getZ(), x = cx - radius;

        while (x <= cx + radius) {
            float z = cz - radius;
            while (z <= cz + radius) {
                float y = sphere ? cy - radius : cy;
                while (true) {
                    float f = y;
                    float f2 = sphere ? cy + radius : (cy + height);
                    if (!(f < f2)) {
                        break;
                    }

                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (!(!(dist < Math.pow(radius, 2)) || hollow && dist < ((radius - 1f) * (radius - 1f)))) {
                        blocks.add(new BlockPos(x, y + yOffset, z));
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }

        return blocks;
    }

    public static List<BlockPos> getCrystalPlacePositions(Vec3d pos, int range, boolean oneDotThirteen) {
        return getSphere(new BlockPos(pos), range, range, false, true, 0)
                .stream()
                .filter(blockPos -> canCrystalBePlacedAt(blockPos, oneDotThirteen))
                .collect(Collectors.toList());
    }

    public static boolean canCrystalBePlacedAt(BlockPos blockPos, boolean oneDotThirteen) {
        try {
            if (!isValidCrystalPlaceBlock(getBlockFromPos(blockPos))) {
                return false;
            }

            BlockPos pos = blockPos.add(0.0, 1.0, 0.0);
            BlockPos pos1 = blockPos.add(0.0, 2.0, 0.0);

            if ((!oneDotThirteen && getBlockFromPos(pos1) != Blocks.AIR) || getBlockFromPos(pos) != Blocks.AIR) {
                return false;
            }

            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity.isDead || entity instanceof EntityEnderCrystal) {
                    continue;
                }

                return false;
            }

            if (!oneDotThirteen) {
                for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos1))) {
                    if (entity.isDead || entity instanceof EntityEnderCrystal) {
                        continue;
                    }

                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean isValidCrystalPlaceBlock(Block block) {
        return block == Blocks.OBSIDIAN || block == Blocks.BEDROCK;
    }

    public static Block getBlockFromPos(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock();
    }

    public static void place(BlockPos pos, EnumHand hand, boolean swing, boolean sneak, boolean packetPlace) {
        if (sneak && !mc.player.isSneaking()) {
            mc.player.setSneaking(true);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionVector().add(0.0, mc.player.getEyeHeight(), 0.0), new Vec3d(pos).add(0.5, 0.5, 0.5));

        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;
        Vec3d hitVec = result == null ? new Vec3d(pos.offset(facing)).add(0.5, 0.5, 0.5).add(new Vec3d(facing.getOpposite().getDirectionVec()).scale(0.5)) : result.hitVec;

        if (packetPlace) {
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, hitVec, hand);
        }

        if (swing) {
            mc.player.swingArm(hand);
        }

        if (sneak && mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.setSneaking(false);
        }
    }

    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing direction : EnumFacing.values()) {
            RayTraceResult result = mc.world.rayTraceBlocks(
                    mc.player.getPositionVector().add(0.0, mc.player.getEyeHeight(), 0.0),
                    new Vec3d(
                            pos.getX() + 0.5 + direction.getDirectionVec().getX() * 1.0 / 2.0,
                            pos.getY() + 0.5 + direction.getDirectionVec().getY() * 1.0 / 2.0,
                            pos.getZ() + 0.5 + direction.getDirectionVec().getZ() * 1.0 / 2.0
                    ),
                    false,
                    true,
                    false
            );

            if (result != null && result.typeOfHit != RayTraceResult.Type.BLOCK || !result.getBlockPos().equals(pos)) {
                continue;
            }

            return direction;
        }

        if (pos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }

        return EnumFacing.UP;
    }
}