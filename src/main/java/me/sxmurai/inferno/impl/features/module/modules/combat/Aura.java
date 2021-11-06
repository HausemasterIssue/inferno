package me.sxmurai.inferno.impl.features.module.modules.combat;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.api.entity.EntityUtil;
import me.sxmurai.inferno.api.entity.InventoryUtil;
import me.sxmurai.inferno.api.timing.TickTimer;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Module.Define(name = "Aura", category = Module.Category.Combat)
@Module.Info(description = "Attacks entities around you")
public class Aura extends Module {
    public final Option<Mode> mode = new Option<>("Mode", Mode.Single);
    public final Option<Priority> priority = new Option<>("Priority", Priority.Closest);
    public final Option<Double> range = new Option<>("Range", 5.0, 1.0, 8.0);
    public final Option<Double> raytrace = new Option<>("Raytrace", 3.0, 1.0, 8.0);
    public final Option<Boolean> frustrum = new Option<>("Frustrum", true);
    public final Option<Boolean> swing = new Option<>("Swing", true);
    public final Option<Rotate> rotate = new Option<>("Rotate", Rotate.Lock);
    public final Option<Timing> timing = new Option<>("Timing", Timing.Vanilla);
    public final Option<Integer> ticks = new Option<>("Ticks", 12, 0, 45, () -> timing.getValue() == Timing.Sequential);
    public final Option<Boolean> packet = new Option<>("Packet", false);
    public final Option<Boolean> sync = new Option<>("Sync", true); // @todo servermanager
    public final Option<Boolean> teleport = new Option<>("Teleport", false);
    public final Option<Weapon> weapon = new Option<>("Weapon", Weapon.Require);
    public final Option<Switch> switchTo = new Option<>("Switch", Switch.Legit, () -> weapon.getValue() == Weapon.Switch);

    public final Option<Boolean> invisible = new Option<>("Invisible", true);
    public final Option<Boolean> players = new Option<>("Players", true);
    public final Option<Boolean> passive = new Option<>("Passive", false);
    public final Option<Boolean> hostile = new Option<>("Hostile", false);

    private final TickTimer timer = new TickTimer();
    private Entity target = null;

    private int oldSlot = -1;

    @Override
    protected void onDeactivated() {
        this.target = null;

        if (fullNullCheck() && this.oldSlot != -1) {
            InventoryUtil.switchTo(this.oldSlot, this.switchTo.getValue() == Switch.Silent);
        }

        this.oldSlot = -1;
    }

    @SubscribeEvent
    public void onEntityAttack(AttackEntityEvent event) {
        if (event.getEntity() == mc.player) {
            this.timer.reset();
        }
    }

    @Override
    public void onTick() {
        if (!this.isValidTarget(this.target) || this.mode.getValue() == Mode.Switch) {
            this.findTarget();
            if (this.target == null) {
                if (this.oldSlot != -1) {
                    InventoryUtil.switchTo(this.oldSlot, this.switchTo.getValue() == Switch.Silent);
                    this.oldSlot = -1;
                }

                return;
            }
        }

        if (!InventoryUtil.isHolding(ItemSword.class, false) && this.weapon.getValue() != Weapon.None) {
            if (this.weapon.getValue() == Weapon.Require) {
                return;
            }

            int slot = InventoryUtil.getHotbarItemSlot(ItemSword.class, false);
            if (slot != -1) {
                this.oldSlot = mc.player.inventory.currentItem;
                InventoryUtil.switchTo(slot, this.switchTo.getValue() == Switch.Silent);
            }
        }

        if (this.rotate.getValue() == Rotate.Lock) {
            Inferno.rotationManager.look(this.target);
        }

        if (this.canAttack()) {
            if (this.rotate.getValue() == Rotate.Attack) {
                Inferno.rotationManager.look(this.target);
            }

            BlockPos current = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            if (this.teleport.getValue()) {
                mc.player.setPosition(this.target.posX, this.target.posY, this.target.posZ);
                mc.player.connection.sendPacket(new CPacketPlayer.Position(this.target.posX, this.target.posY, this.target.posZ, mc.player.onGround));
            }

            if (this.packet.getValue()) {
                mc.player.connection.sendPacket(new CPacketUseEntity(this.target));
            } else {
                mc.playerController.attackEntity(mc.player, this.target);
            }

            if (this.swing.getValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }

            if (this.teleport.getValue()) {
                mc.player.setPosition(current.x, current.y, current.z);
                mc.player.connection.sendPacket(new CPacketPlayer.Position(current.x, current.y, current.z, mc.player.onGround));
            }
        }
    }

    private boolean canAttack() {
        return this.timing.getValue() == Timing.Vanilla ?
                mc.player.getCooledAttackStrength(mc.getRenderPartialTicks()) == 1.0f :
                this.timer.passed(this.ticks.getValue());
    }

    private void findTarget() {
        List<Entity> possibleTargets = EntityUtil.getEntities(this.range.getValue(), this.raytrace.getValue(), this.frustrum.getValue(), (e) -> {
           if (!this.isValidTarget(e) || e.isDead || !(e instanceof EntityLivingBase)) {
               return false;
           }

           if (!this.invisible.getValue() && EntityUtil.isInvisible(e)) {
               return false;
           }

            if (!this.players.getValue() && EntityUtil.isPlayer(e)) {
                return false;
            }

            if (!this.passive.getValue() && EntityUtil.isPassive(e)) {
                return false;
            }

            if (!this.hostile.getValue() && EntityUtil.isHostile(e)) {
                return false;
            }

            if (!e.canBeAttackedWithItem()) {
                return false;
            }

            return true;
        });

        Entity newTarget = null;
        if (!possibleTargets.isEmpty()) {
            for (Entity entity : possibleTargets) {
                if (entity == null || !entity.canBeAttackedWithItem() || !this.isValidTarget(entity)) {
                    continue;
                }

                if (newTarget == null) {
                    newTarget = entity;
                }

                if (this.priority.getValue() == Priority.Closest) {
                    if (EntityUtil.getHealth(entity) < EntityUtil.getHealth(newTarget)) {
                        newTarget = entity;
                    }
                } else {
                    if (mc.player.getDistance(entity) < mc.player.getDistance(newTarget)) {
                        newTarget = entity;
                    }
                }
            }
        }

        this.target = newTarget;
    }

    private boolean isValidTarget(Entity entity) {
        if (entity == null || entity.isDead) {
            return false;
        }

        double dist = mc.player.getDistance(entity);
        return entity != mc.player && (dist <= this.range.getValue() || !mc.player.canEntityBeSeen(entity) && dist <= this.raytrace.getValue());
    }

    public enum Mode {
        Single, Switch
    }

    public enum Priority {
        Closest, Health
    }

    public enum Rotate {
        None, Lock, Attack
    }

    public enum Timing {
        Sequential, Vanilla
    }

    public enum Weapon {
        None, Switch, Require
    }

    public enum Switch {
        Legit, Silent
    }
}
