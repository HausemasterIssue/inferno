package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.api.events.entity.EntityRemoveEvent;
import me.sxmurai.inferno.api.events.entity.EntitySpawnEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.utils.*;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.features.modules.client.Colors;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Module.Define(name = "AutoCrystal", description = "Automatically places and destroys end crystals", category = Module.Category.COMBAT)
public class AutoCrystal extends Module {
    public final Value<Menu> menu = new Value<>("Menu", Menu.PLACE);

    public final Value<Boolean> place = new Value<>("Place", true, (v) -> menu.getValue() == Menu.PLACE);
    public final Value<Float> placeRange = new Value<>("PlaceRange", 4.5f, 1.0f, 8.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Float> placeWallRange = new Value<>("PlaceWallRange", 2.5f, 1.0f, 8.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Integer> placeDelay = new Value<>("PlaceDelay", 4, 0, 50, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Float> placeMinDamage = new Value<>("PlaceMinDamage", 4.0f, 0.1f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Float> placeMaxLocal = new Value<>("PlaceMaxLocal", 12.0f, 0.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Float> faceplaceHealth = new Value<>("FaceplaceHealth", 18.0f, 0.5f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Float> faceplaceDamage = new Value<>("FaceplaceDamage", 2.0f, 0.5f, 6.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Place> placeType = new Value<>("PlaceType", Place.NATIVE, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Integer> places = new Value<>("Places", 2, 1, 4, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Boolean> placePacket = new Value<>("PlacePacket", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue());
    public final Value<Arm> placeSwing = new Value<>("PlaceSwing", Arm.MAIN, (v) -> menu.getValue() == Menu.PLACE && place.getValue());

    public final Value<Boolean> destroy = new Value<>("Destroy", true, (v) -> menu.getValue() == Menu.DESTROY);
    public final Value<Float> destroyRange = new Value<>("DestroyRange", 4.0f, 1.0f, 8.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Float> destroyWallRange = new Value<>("DestroyWallRange", 2.0f, 1.0f, 8.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Integer> destroyDelay = new Value<>("DestroyDelay", 3, 0, 50, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Float> destroyMinDamage = new Value<>("DestroyMinDamage", 2.5f, 0.1f, 36.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Float> destroyMaxLocal = new Value<>("DestroyMaxLocal", 10.0f, 0.0f, 36.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Integer> hits = new Value<>("Hits", 1, 1, 10, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Boolean> destroyPacket = new Value<>("DestroyPacket", true, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());
    public final Value<Arm> destroySwing = new Value<>("DestroySwing", Arm.MAIN, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue());

    public final Value<Boolean> renderDamage = new Value<>("RenderDamage", false, (v) -> menu.getValue() == Menu.RENDER);

    public final Value<Integer> delay = new Value<>("Delay", 0, 0, 50, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Boolean> suicide = new Value<>("Suicide", false, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Float> targetRange = new Value<>("TargetRange", 10.0f, 1.0f, 20.0f, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Priority> priority = new Value<>("Priority", Priority.CLOSEST, (v) -> menu.getValue() == Menu.MISC);
    public final Value<NoFriendPop> noFriendPop = new Value<>("NoFriendPop", NoFriendPop.BOTH, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Rotate> rotate = new Value<>("Rotate", Rotate.BOTH, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Float> minArmor = new Value<>("MinArmor", 12.0f, 0.0f, 120.0f, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Boolean> inhibit = new Value<>("Inhibit", false, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Switch> switchTo = new Value<>("Switch", Switch.NORMAL, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Boolean> pauseOnEat = new Value<>("PauseOnEat", false, (v) -> menu.getValue() == Menu.MISC);
    public final Value<Boolean> pauseOnMine = new Value<>("PauseOnMine", false, (v) -> menu.getValue() == Menu.MISC);

    private EntityPlayer target = null;
    private BlockPos currentPos;
    private EntityEnderCrystal currentCrystal;
    private final RotationUtils.Rotation rotation = new RotationUtils.Rotation(-1.0f, -1.0f);

    private final Queue<BlockPos> positions = new ConcurrentLinkedQueue<>();
    private final Queue<EntityEnderCrystal> crystals = new ConcurrentLinkedQueue<>();

    private final TickTimer switchTimer = new TickTimer();
    private final TickTimer timer = new TickTimer();
    private final TickTimer placeTimer = new TickTimer();
    private final TickTimer destroyTimer = new TickTimer();

    private boolean switching = false;
    private EnumHand hand = EnumHand.MAIN_HAND;

    @Override
    protected void onDeactivated() {
        this.target = null;
        this.currentPos = null;
        this.currentCrystal = null;
        this.rotation.setPitch(-1.0f);
        this.rotation.setYaw(-1.0f);
        this.positions.clear();
        this.crystals.clear();
        this.switchTimer.reset();
        this.timer.reset();
        this.placeTimer.reset();
        this.destroyTimer.reset();
        this.switching = false;
        this.hand = EnumHand.MAIN_HAND;
    }

    @Override
    public void onRender3D(float partialTicks) {
        if (this.currentPos != null) {
            RenderUtils.drawFilledBox(new AxisAlignedBB(this.currentPos).offset(RenderUtils.screen()), Colors.color());
        }

        if (!this.positions.isEmpty()) {
            for (BlockPos pos : this.positions) {
                if (this.currentPos != null && this.currentPos.equals(pos)) {
                    continue;
                }

                RenderUtils.drawFilledBox(new AxisAlignedBB(pos).offset(RenderUtils.screen()), ColorUtils.toRGBA(255, 255, 255, 80));
            }
        }
    }

    @SubscribeEvent
    public void onEntityRemove(EntityRemoveEvent event) {
        if (event.getEntity().equals(this.currentCrystal)) {
            this.currentCrystal = null;
        }
    }

    @SubscribeEvent
    public void onEntityAdded(EntitySpawnEvent event) {
        if (event.getEntity().getPosition().down().equals(this.currentPos)) {
            this.currentPos = null;
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketPlayer) {
            if (((CPacketPlayer) event.getPacket()).rotating) {
                if (this.rotation.getPitch() == -1.0f && this.rotation.getYaw() == -1.0f) {
                    return;
                }

                Inferno.rotationManager.setRotations(this.rotation.getYaw(), this.rotation.getPitch());
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && this.inhibit.getValue()) {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entity : mc.world.loadedEntityList) {
                        if (!(entity instanceof EntityEnderCrystal)) {
                            continue;
                        }

                        if (entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0) {
                            entity.setDead();
                            if (this.currentCrystal == entity) {
                                this.currentCrystal = null;
                            }
                        }
                    }
                }
            } else if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
                for (int id : packet.getEntityIDs()) {
                    Entity entity = mc.world.getEntityByID(id);
                    if (entity == null || entity.isDead || !(entity instanceof EntityEnderCrystal)) {
                        continue;
                    }

                    entity.setDead();
                    if (this.currentCrystal == entity) {
                        this.currentCrystal = null;
                    }
                }
            }
        }
    }

    @Override
    public void onTick() {
        this.switchTimer.tick();
        this.timer.tick();
        this.placeTimer.tick();
        this.destroyTimer.tick();

        if (!this.check()) {
            return;
        }

        this.placeShit(false);
        this.destroyShit();
    }

    private void placeShit(boolean onlyPlace) {
        if (!onlyPlace) {
            this.findPlacePos();
            if (this.currentPos != null && this.positions.size() < this.places.getValue()) {
                this.positions.add(this.currentPos);
                this.currentPos = null;
            }
        }

        if (this.currentCrystal != null) {
            if (!this.currentCrystal.isDead && mc.world.loadedEntityList.contains(this.currentCrystal)) {
                return;
            }

            this.currentCrystal = null;
        }

        if (this.placeTimer.passed(this.placeDelay.getValue())) {
            BlockPos pos = this.positions.poll();
            if (pos == null || !BlockUtil.canCrystalBePlacedAt(pos, this.placeType.getValue() == Place.PROTOCOL)) {
                return;
            }

            double distance = mc.player.getDistance(pos.x, pos.y, pos.z);
            if (distance > this.placeRange.getValue() || !BlockUtil.canSeePos(pos) && distance > this.placeWallRange.getValue()) {
                return;
            }

            this.placeTimer.reset();
            this.currentPos = pos;

            RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5));
            this.rotation.setPitch(rotation.getPitch());
            this.rotation.setYaw(rotation.getYaw());

            BlockUtil.placeCrystal(pos, this.hand, false);

            if (this.placeSwing.getValue() != Arm.NONE) {
                mc.player.swingArm(this.placeSwing.getValue().getArm());
            }
        }
    }

    private void findPlacePos() {
        EntityPlayer target = EntityUtils.getClosest(this.target, this.targetRange.getValue(), false);
        BlockPos pos = null;
        float damage = 0.0f;

        if (this.currentPos != null && this.target != null) {
            target = this.target;
            pos = this.currentPos;

            float selfDamage = DamageUtils.calculateDamage(this.currentPos.x + 0.5, this.currentPos.y + 1.0, this.currentPos.z + 0.5, 6.0f, false, mc.player);
            if (selfDamage + 0.5f > EntityUtils.getHealth(mc.player) || selfDamage + 0.5f > this.placeMaxLocal.getValue()) {
                this.currentPos = null;
                this.findPlacePos();
                return;
            }

            float targetDamage = DamageUtils.calculateDamage(this.currentPos.x + 0.5, this.currentPos.y + 1.0, this.currentPos.z + 0.5, 6.0f, false, this.target);
            if (selfDamage > targetDamage || targetDamage < this.placeMinDamage.getValue()) {
                this.currentPos = null;
                this.findPlacePos();
                return;
            }

            damage = targetDamage;
        }

        List<BlockPos> placements = BlockUtil.getCrystalPlacePositions(mc.player.getPositionVector(), this.placeRange.getValue().intValue(), this.placeType.getValue() == Place.PROTOCOL);
        if (placements.isEmpty()) {
            return;
        }

        p:
        for (BlockPos placePos : placements) {
            if (this.positions.contains(placePos)) {
                continue;
            }

            float selfDamage = DamageUtils.calculateDamage(placePos.x + 0.5, placePos.y + 1.0, placePos.z + 0.5, 6.0f, false, mc.player);
            if (selfDamage + 0.5f > EntityUtils.getHealth(mc.player) || selfDamage + 0.5f > this.placeMaxLocal.getValue()) {
                continue;
            }

            float targetDamage = 0.0f;
            if (target != null) {
                targetDamage = DamageUtils.calculateDamage(placePos.x + 0.5, placePos.y + 1.0, placePos.z + 0.5, 6.0f, false, target);
                if (selfDamage > targetDamage || targetDamage < this.placeMinDamage.getValue()) {
                    continue;
                }
            }

            for (EntityPlayer player : mc.world.playerEntities) {
                float playerDamage = DamageUtils.calculateDamage(placePos.x + 0.5, placePos.y + 1.0, placePos.z + 0.5, 6.0f, false, player);
                if (Inferno.friendManager.isFriend(player)) {
                    if ((this.noFriendPop.getValue() == NoFriendPop.PLACE || this.noFriendPop.getValue() == NoFriendPop.BOTH)) {
                        if (playerDamage + 0.5f > EntityUtils.getHealth(player) || playerDamage > targetDamage) {
                            continue p;
                        }
                    }
                } else {
                    if (this.priority.getValue() == Priority.DAMAGE) {
                        if (playerDamage > damage) {
                            target = player;
                            targetDamage = playerDamage;
                        }
                    }
                }
            }

            if (targetDamage > damage) {
                pos = placePos;
                damage = targetDamage;
            }
        }

        if (!this.positions.contains(pos)) {
            this.currentPos = pos;
            this.target = target;
        }
    }

    private void destroyShit() {
        if (this.target == null) {
            return;
        }

        this.findCrystals();
        if (this.currentCrystal != null) {
            this.crystals.add(this.currentCrystal);
        }

        if (this.currentPos != null) {
            List<EntityEnderCrystal> possibleCrystals = mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.currentPos.up()));
            if (!possibleCrystals.isEmpty()) {
                this.crystals.add(possibleCrystals.get(0));
            }
        }

        if (this.destroyTimer.passed(this.destroyDelay.getValue())) {
            EntityEnderCrystal crystal = this.crystals.poll();
            if (crystal == null) {
                return;
            }

            double dist = mc.player.getDistance(crystal);
            if (dist > this.destroyRange.getValue() || !mc.player.canEntityBeSeen(crystal) && dist > this.destroyWallRange.getValue()) {
                return;
            }

            this.destroyTimer.reset();

            RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
            this.rotation.setPitch(rotation.getPitch());
            this.rotation.setYaw(rotation.getYaw());

            if (this.destroyPacket.getValue()) {
                mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
            } else {
                mc.playerController.attackEntity(mc.player, crystal);
            }

            if (this.destroySwing.getValue() != Arm.NONE) {
                mc.player.swingArm(this.destroySwing.getValue().getArm());
            }
        }
    }

    private void findCrystals() {
        EntityEnderCrystal c = this.currentCrystal;
        float damage = 0.0f;

        if (c != null) {
            float selfDamage = DamageUtils.calculateDamage(c.posX + 0.5, c.posY + 1.0, c.posZ, 6.0f, false, mc.player);
            if (selfDamage + 0.5f > EntityUtils.getHealth(mc.player) || selfDamage + 0.5f > this.destroyMaxLocal.getValue()) {
                this.currentCrystal = null;
                this.findCrystals();
                return;
            }

            float targetDamage = DamageUtils.calculateDamage(c.posX + 0.5, c.posY + 1.0, c.posZ, 6.0f, false, this.target);
            if (selfDamage > targetDamage || targetDamage < this.destroyMinDamage.getValue()) {
                this.currentCrystal = null;
                this.findCrystals();
                return;
            }

            damage = targetDamage;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == null || entity.isDead || !(entity instanceof EntityEnderCrystal)) {
                continue;
            }

            double dist = mc.player.getDistance(entity);
            if (dist > this.destroyRange.getValue() || !mc.player.canEntityBeSeen(entity) && dist > this.destroyWallRange.getValue()) {
                continue;
            }

            EntityEnderCrystal crystal = (EntityEnderCrystal) entity;

            if (this.crystals.equals(crystal)) {
                continue;
            }

            float selfDamage = DamageUtils.calculateDamage(crystal.posX + 0.5, crystal.posY + 1.0, crystal.posZ, 6.0f, false, mc.player);
            if (selfDamage + 0.5f > EntityUtils.getHealth(mc.player) || selfDamage + 0.5f > this.destroyMaxLocal.getValue()) {
                continue;
            }

            float targetDamage = DamageUtils.calculateDamage(crystal.posX + 0.5, crystal.posY + 1.0, crystal.posZ, 6.0f, false, this.target);
            if (selfDamage > targetDamage || targetDamage < this.destroyMinDamage.getValue()) {
                continue;
            }

            if (targetDamage > damage) {
                c = crystal;
                damage = targetDamage;
            }
        }

        this.currentCrystal = c;
    }

    private boolean check() {
        if (!this.timer.passed(this.delay.getValue())) {
            return false;
        } else {
            this.timer.reset();
        }

        if (this.minArmor.getValue() > mc.player.getTotalArmorValue()) {
            return false;
        }

        if (this.pauseOnEat.getValue() && mc.player.isHandActive() && mc.player.getActiveItemStack().getItem() instanceof ItemFood) {
            return false;
        }

        if (this.pauseOnMine.getValue() && mc.playerController.isHittingBlock) {
            return false;
        }

        if (this.priority.getValue() == Priority.CLOSEST) {
            this.target = EntityUtils.getClosest(this.target, this.targetRange.getValue(), false);
            if (this.target == null) {
                return false;
            }
        }

        if (this.switchTo.getValue() != Switch.NONE && this.currentPos != null) {
            if (!this.switching && !InventoryUtils.isHolding(Items.END_CRYSTAL, true)) {
                int slot = InventoryUtils.getHotbarItemSlot(Items.END_CRYSTAL, true);
                if (slot == -1) {
                    return false;
                }

                this.switching = true;
                this.switchTimer.reset();
                this.switchTo(slot);
            }

            if (this.switching) {
                if (!this.switchTimer.passed(8)) {
                    return false;
                }

                this.switchTimer.reset();
                this.switching = false;
            }
        }

        if (!InventoryUtils.isHolding(Items.END_CRYSTAL)) {
            return false;
        }

        return true;
    }

    private boolean switchTo(int slot) {
        if (slot == -1) {
            return false;
        }

        if (slot == 45) {
            this.hand = EnumHand.OFF_HAND;
        } else {
            this.hand = EnumHand.MAIN_HAND;
            InventoryUtils.switchTo(slot, this.switchTo.getValue() == Switch.SILENT);
        }

        mc.player.setActiveHand(this.hand);

        return true;
    }

    public enum Menu {
        PLACE, DESTROY, RENDER, MISC
    }

    public enum Place {
        NATIVE, PROTOCOL
    }

    public enum Arm {
        NONE(null),
        MAIN(EnumHand.MAIN_HAND),
        OFF(EnumHand.OFF_HAND);

        private final EnumHand arm;
        Arm(EnumHand arm) {
            this.arm = arm;
        }

        public EnumHand getArm() {
            return arm;
        }
    }

    public enum Priority {
        CLOSEST, DAMAGE
    }

    public enum NoFriendPop {
        NO_FRIENDS, PLACE, DESTROY, BOTH
    }

    public enum Rotate {
        NONE, PLACE, DESTROY, BOTH
    }

    public enum Switch {
        NONE, NORMAL, SILENT
    }

    private static class Result {
        private final BlockPos pos;
        private final EntityPlayer target;
        private final float damage;

        public Result(BlockPos pos, EntityPlayer target, float damage) {
            this.pos = pos;
            this.target = target;
            this.damage = damage;
        }

        public BlockPos getPos() {
            return pos;
        }

        public EntityPlayer getTarget() {
            return target;
        }

        public float getDamage() {
            return damage;
        }
    }
}
