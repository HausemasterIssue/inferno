package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import me.sxmurai.inferno.utils.RotationUtils;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// @todo fix
@Module.Define(name = "Aura", description = "Attacks things around you", category = Module.Category.COMBAT)
public class Aura extends Module {
    public final Setting<Priority> priority = this.register(new Setting<>("Priority", Priority.CLOSEST));
    public final Setting<Float> targetRange = this.register(new Setting<>("TargetRange", 5.0f, 1.0f, 10.0f));
    public final Setting<Delay> delay = this.register(new Setting<>("Delay", Delay.VANILLA));
    public final Setting<Float> customDelay = this.register(new Setting<>("CustomDelay", 0.0f, 0.0f, 2500.0f, (v) -> delay.getValue() == Delay.CUSTOM));
    public final Setting<Boolean> rotate = this.register(new Setting<>("Rotate", true));
    public final Setting<Boolean> swing = this.register(new Setting<>("Swing", true));
    public final Setting<Boolean> pauseEating = this.register(new Setting<>("PauseEating", true));
    public final Setting<Boolean> pauseMending = this.register(new Setting<>("PauseMending", true));
    public final Setting<Boolean> pauseMining = this.register(new Setting<>("PauseMining", true));
    public final Setting<Float> wallRange = this.register(new Setting<>("WallRange", 2.0f, 0.0f, 5.0f));
    public final Setting<Boolean> packet = this.register(new Setting<>("Packet", false));
    public final Setting<Boolean> players = this.register(new Setting<>("Players", true));
    public final Setting<Boolean> invisible = this.register(new Setting<>("Invisible", false));
    public final Setting<Boolean> mobs = this.register(new Setting<>("Mobs", false));
    public final Setting<Boolean> passive = this.register(new Setting<>("Passive", false));
    public final Setting<Boolean> tpsSync = this.register(new Setting<>("TPSSync", false)); // @todo
    public final Setting<Weapon> weapon = this.register(new Setting<>("Weapon", Weapon.NONE));
    public final Setting<Boolean> autoSwitch = this.register(new Setting<>("AutoSwitch", true, (v) -> weapon.getValue() != Weapon.NONE));
    public final Setting<Boolean> onlyWithWeapon = this.register(new Setting<>("OnlyWithWeapon", false));

    private Entity target = null;
    private final Timer timer = new Timer();
    private int oldSlot = -1;

    @Override
    protected void onDeactivated() {
        target = null;
        timer.reset();

        if (!Module.fullNullCheck() && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        oldSlot = -1;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        findTarget();
        if (target == null || !canAttack()) {
            return;
        }

        if (weapon.getValue() != Weapon.NONE && autoSwitch.getValue() && !InventoryUtils.isHolding(weapon.getValue().clazzTool)) {
            int slot = InventoryUtils.getHotbarItemSlot(weapon.getValue().clazzTool, false);
            if (slot == -1 && onlyWithWeapon.getValue()) {
                return;
            }

            if (slot != -1) {
                oldSlot = mc.player.inventory.currentItem;
                InventoryUtils.switchTo(slot, false);
            }
        }

        if (rotate.getValue()) {
            RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), target.getPositionEyes(mc.getRenderPartialTicks()));
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation.getYaw(), rotation.getPitch(false), mc.player.onGround));
        }

        if (packet.getValue()) {
            mc.player.connection.sendPacket(new CPacketUseEntity(target));
        } else {
            mc.playerController.attackEntity(mc.player, target);
        }

        if (swing.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    private boolean canAttack() {
        return delay.getValue() == Delay.VANILLA ? mc.player.getCooledAttackStrength(0.0f) == 1.0f : delay.getValue() == Delay.CUSTOM && timer.passedMs(customDelay.getValue().longValue());
    }
    
    public boolean pauses() {
    	if (EntityUtils.isEating() && pauseEating.getValue()) {
    		return false;
    	} else if (EntityUtils.isMending() && pauseMending.getValue() ) {
    		return false;
    	} else if (EntityUtils.isMining() && pauseMining.getValue() ) {
    		return false;
    	}
    
    	return true;
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

            if (!players.getValue() && EntityUtils.isPlayer(entity)) {
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
