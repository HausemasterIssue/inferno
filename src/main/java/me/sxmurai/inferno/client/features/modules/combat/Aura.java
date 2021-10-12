package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Aura", description = "Attacks things around you", category = Module.Category.COMBAT)
public class Aura extends Module {
    public final Value<Priority> priority = new Value<>("Priority", Priority.CLOSEST);
    public final Value<Float> targetRange = new Value<>("TargetRange", 5.0f, 1.0f, 10.0f);
    public final Value<Boolean> rotate = new Value<>("Rotate", true);
    public final Value<Boolean> swing = new Value<>("Swing", true);
    public final Value<Float> wallRange = new Value<>("WallRange", 2.0f, 0.0f, 5.0f);
    public final Value<Boolean> packet = new Value<>("Packet", false);
    public final Value<Boolean> players = new Value<>("Players", true);
    public final Value<Boolean> invisible = new Value<>("Invisible", false);
    public final Value<Boolean> mobs = new Value<>("Mobs", false);
    public final Value<Boolean> passive = new Value<>("Passive", false);
    public final Value<Weapon> weapon = new Value<>("Weapon", Weapon.NONE);
    public final Value<Boolean> autoSwitch = new Value<>("AutoSwitch", true, (v) -> weapon.getValue() != Weapon.NONE);
    public final Value<Boolean> onlyWithWeapon = new Value<>("OnlyWithWeapon", false);

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

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        findTarget();
        if (target == null || mc.player.getCooledAttackStrength(0.0f) != 1.0f) {
            if (this.oldSlot != -1) {
                InventoryUtils.switchTo(this.oldSlot, false);
            }

            return;
        }

        if (this.weapon.getValue() != Weapon.NONE && this.autoSwitch.getValue() && !InventoryUtils.isHolding(this.weapon.getValue().clazzTool)) {
            int slot = InventoryUtils.getHotbarItemSlot(this.weapon.getValue().clazzTool, false);
            if (slot == -1 && this.onlyWithWeapon.getValue()) {
                return;
            }

            if (slot != -1) {
                this.oldSlot = mc.player.inventory.currentItem;
                InventoryUtils.switchTo(slot, false);
            }
        }

        if (this.rotate.getValue()) {
            Inferno.rotationManager.look(this.target);
        }

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

    private void findTarget() {
        Entity newTarget = null;

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == null || !EntityUtils.isLiving(entity) || entity == mc.player || entity.isDead || mc.player.getDistance(entity) > targetRange.getValue()) {
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

    public enum Weapon {
        NONE(null),
        SWORD(ItemSword.class),
        AXE(ItemAxe.class);

        private final Class<? extends Item> clazzTool;
        Weapon(Class<? extends Item> clazzTool) {
            this.clazzTool = clazzTool;
        }
    }
}
