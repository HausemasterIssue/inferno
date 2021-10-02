package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.timing.Timer;
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
    public final Setting<Priority> priority = new Setting<>("Priority", Priority.CLOSEST);
    public final Setting<Float> targetRange = new Setting<>("TargetRange", 5.0f, 1.0f, 10.0f);
    public final Setting<Boolean> rotate = new Setting<>("Rotate", true);
    public final Setting<Boolean> swing = new Setting<>("Swing", true);
    public final Setting<Float> wallRange = new Setting<>("WallRange", 2.0f, 0.0f, 5.0f);
    public final Setting<Boolean> packet = new Setting<>("Packet", false);
    public final Setting<Boolean> players = new Setting<>("Players", true);
    public final Setting<Boolean> invisible = new Setting<>("Invisible", false);
    public final Setting<Boolean> mobs = new Setting<>("Mobs", false);
    public final Setting<Boolean> passive = new Setting<>("Passive", false);
    public final Setting<Weapon> weapon = new Setting<>("Weapon", Weapon.NONE);
    public final Setting<Boolean> autoSwitch = new Setting<>("AutoSwitch", true, (v) -> weapon.getValue() != Weapon.NONE);
    public final Setting<Boolean> onlyWithWeapon = new Setting<>("OnlyWithWeapon", false);

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
