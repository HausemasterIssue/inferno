package me.sxmurai.inferno.client.modules.miscellaneous;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.client.modules.player.Speedmine;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.utils.RotationUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

// @todo add switch timer for servers with stricter anticheats
@Module.Define(name = "AutoEChestMiner", description = "Automatically places and mines ender chests")
public class AutoEChestMiner extends Module {
    public static AutoEChestMiner INSTANCE;

    public final Value<Float> range = new Value<>("Range", 5.0f, 1.0f, 8.0f);
    public final Value<Rotate> rotate = new Value<>("Rotate", Rotate.CLIENTSIDE);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Place> place = new Value<>("Place", Place.VANILLA);
    public final Value<Mine> mine = new Value<>("Mine", Mine.VANILLA);
    public final Value<Switch> switchMode = new Value<>("Switch", Switch.NORMAL);

    public BlockPos eChestPos = null;
    private final TickTimer timer = new TickTimer();
    private boolean hitBlockPacket = false;

    public AutoEChestMiner() {
        INSTANCE = this;
    }

    @Override
    protected void onDeactivated() {
        this.eChestPos = null;
        this.timer.reset();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        this.timer.tick();

        if (this.eChestPos != null) {
            if (mc.player.getDistance(this.eChestPos.x, this.eChestPos.y, this.eChestPos.z) > this.range.getValue()) {
                this.eChestPos = null;
                return;
            }

            if (mc.world.isAirBlock(this.eChestPos)) {
                this.hitBlockPacket = false;

                if (this.place.getValue() != Place.NONE) {
                    if (this.switchMode.getValue() != Switch.NONE) {
                        int slot = InventoryUtils.getHotbarBlockSlot(Blocks.ENDER_CHEST, false);
                        if (slot == -1) {
                            return;
                        }

                        InventoryUtils.switchTo(slot, this.switchMode.getValue() == Switch.SILENT);
                    }

                    RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(this.eChestPos.x + 0.5, this.eChestPos.y + 0.5, this.eChestPos.z + 0.5));

                    if (this.rotate.getValue() != Rotate.NONE) {
                        Inferno.rotationManager.setRotations(rotation.getYaw(), rotation.getPitch());
                        if (this.rotate.getValue() == Rotate.CLIENTSIDE) {
                            mc.player.rotationYaw = rotation.getYaw();
                            mc.player.rotationPitch = rotation.getPitch();
                        }
                    }

                    BlockUtil.place(this.eChestPos, EnumHand.MAIN_HAND, this.swing.getValue(), true, this.place.getValue() == Place.PACKET, false);
                }
            }

            if (this.mine.getValue() != Mine.NONE) {
                if (this.switchMode.getValue() == Switch.NONE) {
                    if (!InventoryUtils.isHolding(ItemPickaxe.class, false)) {
                        return;
                    }
                } else {
                    int slot = InventoryUtils.getHotbarItemSlot(ItemPickaxe.class, false);
                    if (slot == -1) {
                        return;
                    }

                    InventoryUtils.switchTo(slot, this.switchMode.getValue() == Switch.SILENT);
                }

                RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(this.eChestPos.x + 0.5, this.eChestPos.y + 0.5, this.eChestPos.z + 0.5));
                if (this.rotate.getValue() != Rotate.NONE) {
                    Inferno.rotationManager.setRotations(rotation.getYaw(), rotation.getPitch());
                    if (this.rotate.getValue() == Rotate.CLIENTSIDE) {
                        mc.player.rotationYaw = rotation.getYaw();
                        mc.player.rotationPitch = rotation.getPitch();
                    }
                }

                if (this.mine.getValue() == Mine.VANILLA) {
                    mc.playerController.onPlayerDamageBlock(this.eChestPos, BlockUtil.getFacing(this.eChestPos));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                } else if (this.mine.getValue() == Mine.PACKET) {
                    if (!Speedmine.INSTANCE.isToggled()) {
                        Speedmine.INSTANCE.toggle();
                    }

                    if (!this.hitBlockPacket) {
                        this.hitBlockPacket = true;
                        mc.playerController.onPlayerDamageBlock(this.eChestPos, BlockUtil.getFacing(this.eChestPos));
                    }
                }
            }
        } else {
            this.findEChest();
            if (this.eChestPos == null) {
                // find a random position, @todo
                return;
            }


        }
    }

    private void findEChest() {
        List<BlockPos> positions = BlockUtil.getSphere(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), this.range.getValue().intValue(), this.range.getValue().intValue(), false, true, 0);
        if (positions.isEmpty()) {
            this.eChestPos = null;
            return;
        }

        BlockPos closest = null;
        for (BlockPos pos : positions) {
            if (BlockUtil.getBlockFromPos(pos) != Blocks.ENDER_CHEST) {
                continue;
            }

            if (closest == null) {
                closest = pos;
            } else {
                if (mc.player.getDistance(pos.x, pos.y, pos.z) < mc.player.getDistance(closest.x, closest.y, closest.z)) {
                    closest = pos;
                }
            }
        }

        this.eChestPos = closest;
    }

    public enum Rotate {
        NONE, PACKET, CLIENTSIDE
    }

    public enum Place {
        NONE, VANILLA, PACKET
    }

    public enum Mine {
        NONE, VANILLA, PACKET
    }

    public enum Switch {
        NONE, NORMAL, SILENT
    }
}
