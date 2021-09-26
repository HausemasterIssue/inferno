package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.entity.EntityRemoveEvent;
import me.sxmurai.inferno.events.entity.EntitySpawnEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.*;
import me.sxmurai.inferno.utils.timing.TickTimer;
import me.sxmurai.inferno.utils.timing.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Module.Define(name = "AutoCrystal", description = "Automatically places and destroys end crystals", category = Module.Category.COMBAT)
public class AutoCrystal extends Module {
    public final Setting<Menu> menu = this.register(new Setting<>("Menu", Menu.TARGET));

    public final Setting<Prioritize> prioritize = this.register(new Setting<>("Prioritize", Prioritize.CLOSEST, (v) -> menu.getValue() == Menu.TARGET));
    public final Setting<Float> targetRange = this.register(new Setting<>("TargetRange", 5.0f, 1.0f, 15.0f, (v) -> menu.getValue() == Menu.TARGET));
    public final Setting<Boolean> naked = this.register(new Setting<>("Naked", false, (v) -> menu.getValue() == Menu.TARGET));

    public final Setting<Boolean> place = this.register(new Setting<>("Place", true, (v) -> menu.getValue() == Menu.PLACE));
    public final Setting<Integer> placeDelay = this.register(new Setting<>("PlaceDelay", 4, 0, 50, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeRange = this.register(new Setting<>("PlaceRange", 4.5f, 1.0f, 6.5f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeWallRange = this.register(new Setting<>("PlaceWallRange", 2.5f, 1.0f, 5.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeMinDmg = this.register(new Setting<>("PlaceMinDamage", 4.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeMaxSelfDmg = this.register(new Setting<>("PlaceMaxSelfDamage", 10.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> faceplaceHealth = this.register(new Setting<>("FaceplaceHealth", 12.0f, 0.5f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> faceplaceMinDamage = this.register(new Setting<>("FaceplaceMinDamage", 2.0f, 1.0f, 8.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> placeRotate = this.register(new Setting<>("PlaceRotate", true, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Arm> placeSwing = this.register(new Setting<>("PlaceSwing", Arm.MAINHAND, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> oneDot13 = this.register(new Setting<>("1.13", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> packetPlace = this.register(new Setting<>("PacketPlace", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Integer> maxPlaces = this.register(new Setting<>("MaxPlaces", 1, 1, 4, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> predict = this.register(new Setting<>("Predict", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> predictDelay = this.register(new Setting<>("PredictDelay", 12.0f, 0.0f, 500.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue() && predict.getValue()));

    public final Setting<Boolean> hit = this.register(new Setting<>("Hit", true, (v) -> menu.getValue() == Menu.HIT));
    public final Setting<Integer> hitDelay = this.register(new Setting<>("HitDelay", 3, 0, 50, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Float> hitRange = this.register(new Setting<>("HitRange", 4.0f, 1.0f, 6.5f, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Float> hitWallRange = this.register(new Setting<>("HitWallRange", 2.0f, 1.0f, 6.5f, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Float> hitMinDamage = this.register(new Setting<>("HitMinDamage", 3.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Float> hitMaxSelfDmg = this.register(new Setting<>("HitMaxSelfDamage", 11.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Boolean> hitRotate = this.register(new Setting<>("HitRotate", true, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Arm> hitSwing = this.register(new Setting<>("HitSwing", Arm.MAINHAND, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Boolean> packetHit = this.register(new Setting<>("PacketHit", true, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Integer> maxPerTick = this.register(new Setting<>("MaxHitsPerTick", 2, 1, 15, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Boolean> hitRemove = this.register(new Setting<>("HitRemove", false, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));
    public final Setting<Hit> hitMode = this.register(new Setting<>("HitMode", Hit.ALL, (v) -> menu.getValue() == Menu.HIT && hit.getValue()));

    // @todo render settings

    public final Setting<Integer> cooldown = this.register(new Setting<>("Cooldown", 10, 0, 500, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> antiSuicide = this.register(new Setting<>("AntiSuicide", true, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Logic> logic = this.register(new Setting<>("Logic", Logic.BREAKPLACE, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Integer> minArmor = this.register(new Setting<>("MinArmor", 20, 0, 125, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<AntiFriendPop> antiFriendPop = this.register(new Setting<>("AntiFriendPop", AntiFriendPop.HIT, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> inhibit = this.register(new Setting<>("Inhibit", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Double> randomRotations = this.register(new Setting<>("RandomRotations", 0.0, 0.0, 6.0f, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> yawStep = this.register(new Setting<>("YawStep", true, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Integer> yawSteps = this.register(new Setting<>("YawSteps", 6, 2, 20, (v) -> menu.getValue() == Menu.MISCELLANEOUS && yawStep.getValue()));
    public final Setting<Boolean> sync = this.register(new Setting<>("Sync", true, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> randomPause = this.register(new Setting<>("RandomPause", true, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> extraCalc = this.register(new Setting<>("ExtraCalc", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> stopOnMine = this.register(new Setting<>("StopOnMine", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> stopOnEat = this.register(new Setting<>("StopOnEat", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> autoSwitch = this.register(new Setting<>("AutoSwitch", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS));
    public final Setting<Boolean> switchBack = this.register(new Setting<>("SwitchBack", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS && autoSwitch.getValue()));
    public final Setting<Boolean> silentSwitch = this.register(new Setting<>("SilentSwitch", false, (v) -> menu.getValue() == Menu.MISCELLANEOUS && autoSwitch.getValue()));

    private EntityPlayer target = null;

    private final Queue<BlockPos> positions = new ConcurrentLinkedQueue<>();
    private final Queue<EntityEnderCrystal> crystals = new ConcurrentLinkedQueue<>();
    private int inhibitHits = 0;

    private final TickTimer cooldownTimer = new TickTimer();
    private final TickTimer placeTimer = new TickTimer();
    private final Timer predictTimer = new Timer();
    private final TickTimer hitTimer = new TickTimer();
    private final TickTimer inhibitTimer = new TickTimer();

    private EntityEnderCrystal currentCrystal = null;
    private BlockPos currentPos = null;
    private float currentDamage = 0.0f;

    private final RotationUtils.Rotation rotation = new RotationUtils.Rotation(-1.0f, -1.0f);
    private boolean rotating = false;

    private int oldSlot = -1;
    private EnumHand hand;

    @Override
    protected void onDeactivated() {
        this.target = null;

        this.positions.clear();
        this.crystals.clear();
        this.inhibitHits = 0;

        this.cooldownTimer.reset();
        this.placeTimer.reset();
        this.predictTimer.reset();
        this.hitTimer.reset();
        this.inhibitTimer.reset();

        this.currentCrystal = null;
        this.currentPos = null;
        this.currentDamage = 0.0f;

        this.rotation.setYaw(-1.0f);
        this.rotation.setPitch(-1.0f);
        this.rotating = false;

        this.oldSlot = -1;
        this.hand = null;
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        if (!this.positions.isEmpty()) {
            for (BlockPos pos : this.positions) {
                RenderUtils.drawFilledBox(new AxisAlignedBB(pos).offset(RenderUtils.screen()), ColorUtils.toRGBA(255, 255, 255, 80));
            }
        }

        if (this.currentPos != null) {
            RenderUtils.drawFilledBox(new AxisAlignedBB(this.currentPos).offset(RenderUtils.screen()), ColorUtils.toRGBA(255, 0, 0, 80));
        }

        if (this.currentCrystal != null) {
            RenderUtils.drawFilledBox(new AxisAlignedBB(this.currentCrystal.getPosition().down()).offset(RenderUtils.screen()), ColorUtils.toRGBA(0, 255, 0, 80));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof CPacketPlayer) {
                CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                if (packet.rotating && this.rotating) {
                    if (this.yawStep.getValue()) {
                        if (Math.abs(this.rotation.getYaw() - mc.player.rotationYaw) >= 20 || Math.abs(this.rotation.getPitch() - mc.player.rotationPitch) >= 20) {
                            for (float step = this.yawSteps.getValue().floatValue() - 1.0f; step > 0; step--) {
                                Inferno.rotationManager.setRotations(this.rotation.getYaw() / step + 1.0f, this.rotation.getPitch() / step + 1.0f);
                            }
                        }
                    }

                    packet.yaw = this.rotation.getYaw();
                    packet.pitch = this.rotation.getPitch();

                    Inferno.rotationManager.setRotations(this.rotation.getYaw(), this.rotation.getPitch());
                }
            } else if (event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
                if (this.currentCrystal != null && packet.entityId == this.currentCrystal.entityId && this.hitRemove.getValue()) {
                    mc.world.removeEntityFromWorld(packet.entityId);
                    this.currentCrystal = null;

                    ++this.inhibitHits;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck() && this.sync.getValue()) {
            if (event.getPacket() instanceof SPacketDestroyEntities) {
                SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();

                for (int id : packet.getEntityIDs()) {
                    Entity entity = mc.world.getEntityByID(id);
                    if (!(entity instanceof EntityEnderCrystal) || entity.isDead) {
                        continue;
                    }

                    if (this.currentCrystal == entity) {
                        this.currentCrystal = null;
                        this.rotating = false;
                    }

                    this.crystals.remove(entity);
                }
            } else if (event.getPacket() instanceof SPacketSoundEffect) {
                SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    for (Entity entity : mc.world.loadedEntityList) {
                        if (entity instanceof EntityEnderCrystal && entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0f) {
                            entity.setDead();
                        }
                    }
                }
            } else if (event.getPacket() instanceof SPacketExplosion) {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal && entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= packet.getStrength()) {
                        entity.setDead();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof EntityEnderCrystal && event.getEntity().getPosition().down().equals(this.currentPos)) {
            this.currentPos = null;
            this.rotating = false;
        }
    }

    @SubscribeEvent
    public void onEntityRemove(EntityRemoveEvent event) {
        if (event.getEntity() instanceof EntityEnderCrystal && this.currentCrystal == event.getEntity()) {
            this.currentCrystal = null;
            this.rotating = false;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Module.fullNullCheck()) {
            return;
        }

        this.cooldownTimer.tick();
        this.placeTimer.tick();
        this.hitTimer.tick();

        if (!this.check()) {
            return;
        }

        this.doAutoCrystal();
    }

    private void doAutoCrystal() {
        this.cleanup();

        // update rotations
        if (this.rotating) {
            if (this.currentCrystal != null) {
                this.look(this.currentCrystal.getPositionEyes(mc.getRenderPartialTicks()));
            } else {
                if (this.currentPos != null) {
                    this.look(new Vec3d(this.currentPos.x + 0.5, this.currentPos.y - 0.5, this.currentPos.z + 0.5));
                }
            }
        }

        if (this.prioritize.getValue() == Prioritize.CLOSEST) {
            this.target = this.getClosest();
            if (this.target == null) {
                return;
            }
        }

        if (this.logic.getValue() == Logic.PLACEBREAK) {
            this.doPlace(false);
            this.doHit(false);
        } else if (this.logic.getValue() == Logic.BREAKPLACE) {
            this.doHit(false);
            this.doPlace(false);
        }
    }

    private void doPlace(boolean cleanup) {
        while (!this.positions.isEmpty()) {
            if (!this.placeTimer.passed(this.placeDelay.getValue())) {
                break;
            }

            BlockPos pos = this.positions.poll();
            if (pos == null) {
                continue;
            }

            this.placeTimer.reset();

            double dist = mc.player.getDistance(pos.x, pos.y, pos.z);
            if (dist > this.placeRange.getValue() || !BlockUtil.canSeePos(pos) && dist > this.placeWallRange.getValue()) {
                continue;
            }

            if (this.extraCalc.getValue()) {
                // @todo
            }

            this.place(this.currentPos = pos);
        }

        if (cleanup) {
            return;
        }

        for (int i = 0; i < this.maxPlaces.getValue(); ++i) {
            if (this.positions.size() + (this.currentPos == null ? 0 : 1) > this.maxPlaces.getValue()) {
                break;
            }

            this.calculatePlacements();
            if (this.currentPos != null && this.currentDamage != 0.0f) {
                if (!this.positions.contains(this.currentPos)) {
                    this.positions.add(this.currentPos);
                }
            }

            this.currentPos = null;
            this.currentDamage = 0.0f;
        }

        if (this.placeTimer.passed(this.placeDelay.getValue())) {
            this.doPlace(true);
        }
    }

    private void calculatePlacements() {
        EntityPlayer t = this.target;
        BlockPos p = null;
        float d = 0.0f;

        if (this.currentPos != null && t != null) {
            Result result = this.doSinglePlaceCalculation(this.currentPos, t);
            if (result == null) {
                this.currentPos = null;
                this.currentDamage = 0.0f;
            }
        }

        List<BlockPos> placements = BlockUtil.getCrystalPlacePositions(mc.player.getPositionVector(), this.placeRange.getValue().intValue(), this.oneDot13.getValue());
        if (!placements.isEmpty()) {
            for (BlockPos pos : placements) {
                if (pos == null || this.positions.contains(pos) || this.currentPos == pos) {
                    continue;
                }

                Result result = this.doSinglePlaceCalculation(pos, t);
                if (result == null) {
                    continue;
                }

                if (result.getDamage() > d) {
                    d = result.getDamage();
                    p = result.getPos();
                    t = result.getEfficientTarget();
                }
            }
        }

        if (p == null && d == 0.0f && t != null) {
            if (EntityUtils.getHealth(t) <= this.faceplaceHealth.getValue()) {
                for (EnumFacing facing : EnumFacing.values()) {
                    if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                        continue;
                    }

                    BlockPos surroundingPos = new BlockPos(t.posX, t.posY, t.posZ).offset(facing);
                    double dist = mc.player.getDistance(surroundingPos.x, surroundingPos.y, surroundingPos.z);
                    if (dist > this.placeRange.getValue() || !BlockUtil.canSeePos(surroundingPos) && dist > this.placeWallRange.getValue()) {
                        continue;
                    }

                    if (!BlockUtil.canCrystalBePlacedAt(surroundingPos, this.oneDot13.getValue())) {
                        continue;
                    }

                    Result result = this.doSinglePlaceCalculation(surroundingPos, t);
                    if (result == null) {
                        continue;
                    }

                    if (result.getDamage() < this.faceplaceMinDamage.getValue()) {
                        continue;
                    }

                    if (p == null) {
                        p = result.getPos();
                    } else {
                        if (result.getDamage() > d) {
                            p = result.getPos();
                            d = result.getDamage();
                        }
                    }
                }
            }
        }

        this.target = t;
        this.currentPos = p;
        this.currentDamage = d;
    }

    private Result doSinglePlaceCalculation(BlockPos pos, EntityPlayer currentTarget) {
        EntityPlayer newTarget = currentTarget;

        float selfDamage = DamageUtils.calculateDamage(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, 6.0f, false, mc.player);
        if (this.antiSuicide.getValue()) {
            if (selfDamage + 0.5f >= this.placeMaxSelfDmg.getValue() || selfDamage + 0.5f > EntityUtils.getHealth(mc.player)) {
                return null;
            }
        }

        float targetDamage = 0.0f;
        if (newTarget != null) {
            targetDamage = DamageUtils.calculateDamage(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, 6.0f, false, newTarget);
            if (selfDamage > targetDamage || targetDamage < this.placeMinDmg.getValue()) {
                return null;
            }
        }

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == mc.player || mc.player.getDistance(player) > this.targetRange.getValue()) {
                continue;
            }

            float playerDamage = DamageUtils.calculateDamage(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, 6.0f, false, player);

            if (Inferno.friendManager.isFriend(player) && (this.antiFriendPop.getValue() == AntiFriendPop.PLACE || this.antiFriendPop.getValue() == AntiFriendPop.ALL)) {
                if (playerDamage > targetDamage || playerDamage + 0.5f > EntityUtils.getHealth(player)) {
                    continue;
                }
            }

            if (this.prioritize.getValue() == Prioritize.DAMAGE && playerDamage > targetDamage) {
                newTarget = player;
                targetDamage = playerDamage;
            }
        }

        return new Result(newTarget, pos, targetDamage);
    }

    private void doHit(boolean cleanup) {
        while (!this.crystals.isEmpty()) {
            EntityEnderCrystal crystal = this.crystals.poll();
            if (crystal == null) {
                continue;
            }

            double dist = mc.player.getDistance(crystal);
            if (dist > this.hitRange.getValue() || !mc.player.canEntityBeSeen(crystal) && dist > this.hitWallRange.getValue()) {
                continue;
            }

            if (!this.hitTimer.passed(this.hitDelay.getValue())) {
                break;
            }

            this.hitTimer.reset();

            if (this.extraCalc.getValue()) {
                // @todo
            }

            this.hit(this.currentCrystal = crystal);
        }

        if (cleanup) {
            return;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == null || entity.isDead || !(entity instanceof EntityEnderCrystal) || this.currentCrystal == entity) {
                continue;
            }

            EntityEnderCrystal crystal = (EntityEnderCrystal) entity;
            double dist = mc.player.getDistance(crystal);

            if (dist > this.hitRange.getValue() || !mc.player.canEntityBeSeen(crystal) && dist > this.hitWallRange.getValue()) {
                continue;
            }

            if (this.crystals.contains(crystal)) {
                continue;
            }

            if (this.hitMode.getValue() == Hit.ALL) {
                this.crystals.add(crystal);
            } else {
                BlockPos crystalPos = crystal.getPosition().down();
                if (this.positions.stream().anyMatch(crystalPos::equals) || crystalPos.equals(this.currentPos)) {
                    this.crystals.add(crystal);
                }
            }
        }

        if (this.hitTimer.passed(this.hitDelay.getValue())) {
            this.doHit(true);
        }
    }

    private void cleanup() {
        if (this.logic.getValue() == Logic.PLACEBREAK) {
            this.doPlace(true);
            this.doHit(true);
        } else if (this.logic.getValue() == Logic.BREAKPLACE) {
            this.doHit(true);
            this.doPlace(true);
        }
    }

    private boolean check() {
//        if (this.minArmor.getValue() < mc.player.getTotalArmorValue()) {
//            return false;
//        }

        if (!this.cooldownTimer.passed(this.cooldown.getValue())) {
            return false;
        }

        this.cooldownTimer.reset();

        if (this.inhibit.getValue()) {
            if (this.inhibitHits < 40) {
                this.inhibitTimer.reset();
            } else {
                this.inhibitTimer.tick();
                if (!this.inhibitTimer.passed(4)) {
                    return false;
                }

                this.inhibitHits = 0;
            }
        }

        if (InventoryUtils.isHolding(Items.END_CRYSTAL)) {
            this.hand = EnumHand.MAIN_HAND;
        } else {
            return false;
        }

//        if (this.autoSwitch.getValue() && this.oldSlot == -1) {
//            this.doSwitch();
//            if (this.oldSlot == -1 || this.hand == null) {
//                return false;
//            }
//        }

        return true;
    }

    private void doSwitch() {
        int slot = InventoryUtils.getHotbarItemSlot(Items.END_CRYSTAL, true);
        if (slot == -1) {
            this.oldSlot = -1;
            this.hand = null;
            return;
        }

        if (slot == 45) {
            this.hand = EnumHand.OFF_HAND;
        } else {
            this.hand = EnumHand.MAIN_HAND;
            this.oldSlot = mc.player.inventory.currentItem;

            InventoryUtils.switchTo(slot, this.silentSwitch.getValue());
        }
    }

    private void place(BlockPos pos) {
        this.currentPos = pos;
        this.currentCrystal = null;

        if (this.placeRotate.getValue()) {
            this.look(new Vec3d(pos.x + 0.5, pos.y - 0.5, pos.z + 0.5));
        }

        BlockUtil.placeNormal(pos, this.hand, false, true, this.packetPlace.getValue(), false);

        if (this.placeSwing.getValue() != Arm.NONE) {
            mc.player.swingArm(this.placeSwing.getValue().getHand());
        }
    }

    private void hit(EntityEnderCrystal crystal) {
        this.currentCrystal = crystal;
        this.currentPos = null;

        if (this.hitRotate.getValue()) {
            this.look(crystal.getPositionEyes(mc.getRenderPartialTicks()));
        }

        if (this.packetHit.getValue()) {
            mc.player.connection.sendPacket(new CPacketUseEntity(crystal));
        } else {
            mc.playerController.attackEntity(mc.player, crystal);
        }

        if (this.hitSwing.getValue() != Arm.NONE) {
            ++this.inhibitHits;
            mc.player.swingArm(this.hitSwing.getValue().getHand());
        }
    }

    private void look(Vec3d pos) {
        RotationUtils.Rotation rotation = RotationUtils.calcRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), pos);

        this.rotating = true;
        this.rotation.setYaw(rotation.getYaw() + this.getRandomRotation());
        this.rotation.setPitch(rotation.getPitch() + this.getRandomRotation());
    }

    private float getRandomRotation() {
        return this.randomRotations.getValue().floatValue() == 0.0f ? 0.0f : (float) ThreadLocalRandom.current().nextDouble(-this.randomRotations.getValue(), this.randomRotations.getValue());
    }

    private EntityPlayer getClosest() {
        EntityPlayer player = null;
        for (EntityPlayer p : mc.world.playerEntities) {
            if (p == null || p.isDead || p != mc.player) {
                continue;
            }

            if (!this.naked.getValue() && p.getTotalArmorValue() == 0) {
                continue;
            }

            double dist = mc.player.getDistance(p);
            if (dist > this.targetRange.getValue()) {
                continue;
            }

            if (mc.player.getDistance(p) > dist) {
                player = p;
            }
        }

        return player;
    }

    public enum Menu {
        TARGET, PLACE, HIT, RENDER, MISCELLANEOUS
    }

    public enum Prioritize {
        CLOSEST, DAMAGE
    }

    public enum Arm {
        NONE(null),
        MAINHAND(EnumHand.MAIN_HAND),
        OFFHAND(EnumHand.OFF_HAND);

        private final EnumHand hand;
        Arm(EnumHand hand) {
            this.hand = hand;
        }

        public EnumHand getHand() {
            return hand;
        }
    }

    public enum Hit {
        OWN, ALL
    }

    public enum AntiFriendPop {
        NOFRIENDS, PLACE, HIT, ALL
    }

    public enum Logic {
        PLACEBREAK, BREAKPLACE
    }

    private static class Result {
        private final EntityPlayer efficientTarget;
        private final BlockPos pos;
        private final float damage;

        public Result(EntityPlayer efficientTarget, BlockPos pos, float damage) {
            this.efficientTarget = efficientTarget;
            this.pos = pos;
            this.damage = damage;
        }

        public EntityPlayer getEfficientTarget() {
            return efficientTarget;
        }

        public BlockPos getPos() {
            return pos;
        }

        public float getDamage() {
            return damage;
        }
    }
}
