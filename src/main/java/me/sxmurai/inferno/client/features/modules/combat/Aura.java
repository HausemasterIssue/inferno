package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

@Module.Define(name = "Aura", description = "Attacks things around you", category = Module.Category.COMBAT)
public class Aura extends Module {
    public final Value<Priority> priority = new Value<>("Priority", Priority.CLOSEST);
    public final Value<Float> range = new Value<>("Range", 5.0f, 1.0f, 10.0f);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Float> wallRange = new Value<>("WallRange", 2.0f, 0.0f, 5.0f);
    public final Value<Boolean> packet = new Value<>("Packet", false);
    public final Value<Boolean> players = new Value<>("Players", true);
    public final Value<Boolean> invisible = new Value<>("Invisible", false);
    public final Value<Boolean> mobs = new Value<>("Mobs", false);
    public final Value<Boolean> passive = new Value<>("Passive", false);
    public final Value<Boolean> swordOnly = new Value<>("SwordOnly", true);
    public final Value<Boolean> autoSwitch = new Value<>("AutoSwitch", false, (v) -> swordOnly.getValue());

    private Entity target = null;
    private int oldSlot = -1;

    @Override
    protected void onDeactivated() {
        this.target = null;

        if (!Module.fullNullCheck() && this.oldSlot != -1) {
            InventoryUtils.switchTo(this.oldSlot, false);
        }

        this.oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        this.findTarget();
        if (target == null) {
            if (this.autoSwitch.getValue() && this.oldSlot != -1) {
                InventoryUtils.switchTo(this.oldSlot, false);
                this.oldSlot = -1;
            }

            return;
        }

        if (this.swordOnly.getValue()) {
            if (!InventoryUtils.isHolding(ItemSword.class, false)) {
                if (!this.autoSwitch.getValue()) {
                    return;
                }

                int slot = InventoryUtils.getHotbarItemSlot(ItemSword.class, false);
                if (slot == -1) {
                    return;
                }

                this.oldSlot = mc.player.inventory.currentItem;
                InventoryUtils.switchTo(slot, false);
            }
        }

        if (this.rotate.getValue()) {
            Inferno.rotationManager.look(this.target);
        }

        if (mc.player.getCooledAttackStrength(0.0f) == 1.0f) {
            if (this.packet.getValue()) {
                mc.player.connection.sendPacket(new CPacketUseEntity(this.target));
                mc.player.resetCooldown();
            } else {
                mc.playerController.attackEntity(mc.player, this.target);
            }

            if (this.swing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    private void findTarget() {
        Entity newTarget = null;

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == null || !EntityUtils.isLiving(entity) || entity == mc.player || entity.isDead || mc.player.getDistance(entity) > range.getValue()) {
                continue;
            }

            if (!mc.player.canEntityBeSeen(entity) && mc.player.getDistance(entity) > wallRange.getValue()) {
                continue;
            }

            if (!invisible.getValue() && entity instanceof EntityLiving && EntityUtils.isInvisible((EntityLiving) entity)) {
                continue;
            }

            if (!players.getValue() && entity instanceof EntityPlayer) {
                continue;
            }

            if (!mobs.getValue() && EntityUtils.isHostile(entity)) {
                continue;
            }

            if (!passive.getValue() && EntityUtils.isPassive(entity)) {
                continue;
            }

            if (newTarget == null) {
                newTarget = entity;
                continue;
            }

            if (priority.getValue() == Priority.CLOSEST) {
                if (mc.player.getDistance(newTarget) > mc.player.getDistance(entity)) {
                    newTarget = entity;
                }
            } else if (priority.getValue() == Priority.HEALTH) {
                if (EntityUtils.getHealth(newTarget) > EntityUtils.getHealth(entity)) {
                    newTarget = entity;
                }
            }
        }

        if (newTarget == mc.player) {
            target = null;
            return;
        }

        target = newTarget;
    }

    public enum Priority {
        CLOSEST, HEALTH
    }

    public enum Delay {
        VANILLA, CUSTOM
    }
}
