package me.sxmurai.inferno.api.utils;

import me.sxmurai.inferno.client.Inferno;
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

public class BlockUtil extends Wrapper {
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
        // we get a sphere of blocks around a player within a range, we filter out the places where it cannot be placed, and we return that list of blocks
        return getSphere(new BlockPos(pos), range, range, false, true, 0)
                .stream()
                .filter(blockPos -> canCrystalBePlacedAt(blockPos, oneDotThirteen))
                .collect(Collectors.toList());
    }

    // @todo rewrite
    public static boolean canCrystalBePlacedAt(BlockPos blockPos, boolean oneDotThirteen) {
        try {
            // if the block the crystal is being placed on is not bedrock or obsidian, we cant place there
            if (!isValidCrystalPlaceBlock(getBlockFromPos(blockPos))) {
                return false;
            }

            // we get the position from one up
            BlockPos pos = blockPos.add(0.0, 1.0, 0.0);
            // we get the position from two up
            BlockPos pos1 = blockPos.add(0.0, 2.0, 0.0);

            // if no 1.13 placements and the block above the block isnt air, or the first block above isnt air, we cant place because of non 1.13 placements
            if ((!oneDotThirteen && getBlockFromPos(pos1) != Blocks.AIR) || getBlockFromPos(pos) != Blocks.AIR) {
                return false;
            }

            // make sure theres no entities to interfere with placements
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity.isDead || entity instanceof EntityEnderCrystal) {
                    continue;
                }

                return false;
            }

            // if no 1.13 place, we have to also check two blocks above
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
        // get the block type from a pos
        return mc.world.getBlockState(pos).getBlock();
    }

    public static boolean canSeePos(BlockPos pos) {
        // this is basically taken from the minecraft decompiled code, but just modified for block positions instead of entities
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos), false, true, false) == null;
    }

    public static void place(BlockPos pos, EnumHand hand, boolean swing, boolean sneak, boolean packet, boolean rotate) {
        for (EnumFacing direction : EnumFacing.values()) {
            // get the neighboring block of "pos"
            BlockPos neighbor = pos.offset(direction);
            // if we cannot replace the block (isnt a liquid, isnt air, etc) or entities intersect with the neighboring pos, continue
            if (mc.world.isAirBlock(neighbor) || intersectsWith(neighbor)) {
                continue;
            }

            // if we get past the above if statement, we can use this direction to place the block

            // sneak if provided as true
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            // rotate
            if (rotate) {
                Inferno.rotationManager.look(new Vec3d(neighbor.x + 0.5, neighbor.y + 0.5, neighbor.z + 0.5));
            }

            // the hitvec, i need to learn more about this
            Vec3d hitVec = new Vec3d(neighbor.x + 0.5, neighbor.y + 0.5, neighbor.z + 0.5).add(new Vec3d(direction.getOpposite().getDirectionVec()).scale(0.5));

            // we want to place the block on the neighboring block so that it places where we want. we also want the direction facing to be opposite because if the facing block is
            // lets say south, thats not where we're facing. so we'd need th opposite of that
            if (packet) {
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbor, direction.getOpposite(), hand, (float) (hitVec.x - pos.x), (float) (hitVec.y - pos.y), (float) (hitVec.z - pos.z)));
            } else {
                mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, direction.getOpposite(), hitVec, hand);
            }

            // swing
            if (swing) {
                mc.player.swingArm(hand);
            }

            // if we had sneak on, we had already sent a START_SNEAKING packet, so we need to stop sneaking
            if (sneak) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            return;
        }
    }

    public static void placeCrystal(BlockPos pos, EnumHand hand, boolean swing) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5));
        EnumFacing facing = result == null || result.sideHit == null ? EnumFacing.UP : result.sideHit;

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0f, 0.0f, 0.0f));

        if (swing) {
            mc.player.swingArm(hand);
        }
    }

    // @todo rewrite
    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing direction : EnumFacing.values()) {
            RayTraceResult result = mc.world.rayTraceBlocks(
                    new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
                    new Vec3d(
                            pos.x + 0.5 + direction.getDirectionVec().x * 1.0 / 2.0,
                            pos.x + 0.5 + direction.getDirectionVec().y * 1.0 / 2.0,
                            pos.x + 0.5 + direction.getDirectionVec().z * 1.0 / 2.0
                    ),
                    false,
                    true,
                    false
            );

            if (result != null && (result.typeOfHit != RayTraceResult.Type.BLOCK || !result.getBlockPos().equals(pos))) {
                continue;
            }

            return direction;
        }

        return pos.y > mc.player.posY + mc.player.getEyeHeight() ? EnumFacing.DOWN : EnumFacing.UP;
    }

    public static boolean intersectsWith(BlockPos pos) {
        return !mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos), (v) -> v != null && !v.isDead).isEmpty();
    }
}