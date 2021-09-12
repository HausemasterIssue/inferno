package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

@Module.Define(name = "AutoCrystal", description = "Automatically places and destroys end crystals", category = Module.Category.COMBAT)
public class AutoCrystal extends Module {
    public final Setting<Menu> menu = this.register(new Setting<>("Menu", Menu.PLACE));

    // place options
    public final Setting<Boolean> place = this.register(new Setting<>("Place", true, (v) -> menu.getValue() == Menu.PLACE));
    public final Setting<Float> placeRange = this.register(new Setting<>("PlaceRange", 4.5f, 1.0f, 7.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    // add this: public final Setting<Boolean> yawStep = this.register(new Setting<>("YawStep", true, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    // and this: public final Setting<Float> yawStepTicks = this.register(new Setting<>("YawStepTicks", 1.0f, 1.0f, 10.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue() && yawStep.getValue()));
    public final Setting<Float> placeWallRange = this.register(new Setting<>("PlaceWallRange", 2.5f, 1.0f, 5.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeDelay = this.register(new Setting<>("PlaceDelay", 1.0f, 0.0f, 20.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeMinDamage = this.register(new Setting<>("PlaceMinDmg", 6.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> placeMaxSelfDamage = this.register(new Setting<>("PlaceMaxSelfDmg", 10.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> facePlaceHealth = this.register(new Setting<>("FacePlaceHealth", 16.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> facePlaceMinDamage = this.register(new Setting<>("FacePlaceMinDmg", 2.0f, 1.0f, 10.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> oneDot13Place = this.register(new Setting<>("1.13", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Integer> maxCrystalPlace = this.register(new Setting<>("WasteAmount", 2, 1, 5, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> placeRotate = this.register(new Setting<>("PlaceRotate", true, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    // and this: public final Setting<Boolean> strictDirection = this.register(new Setting<>("StrictDirection", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Hand> placeSwing = this.register(new Setting<>("PlaceSwing", Hand.CORRECT, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> placePacket = this.register(new Setting<>("PacketPlace", false, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> predict = this.register(new Setting<>("Predict", true, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Float> predictDelay = this.register(new Setting<>("PredictDelay", 5.0f, 0.0f, 500.0f, (v) -> menu.getValue() == Menu.PLACE && place.getValue() && predict.getValue()));

    // destroy options
    public final Setting<Boolean> destroy = this.register(new Setting<>("Destroy", true, (v) -> menu.getValue() == Menu.DESTROY));
    public final Setting<Float> destroyRange = this.register(new Setting<>("DestroyRange", 4.0f, 1.0f, 7.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Float> destroyWallRange = this.register(new Setting<>("DestroyWallRange", 3.5f, 1.0f, 5.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Float> destroyDelay = this.register(new Setting<>("DestroyDelay", 1.0f, 0.0f, 20.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Float> destroyMinDamage = this.register(new Setting<>("DestroyMinDmg", 6.5f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Float> destroyMaxSelfDamage = this.register(new Setting<>("DestroyMaxSelfDmg", 10.0f, 1.0f, 36.0f, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Boolean> destroyRotate = this.register(new Setting<>("DestroyRotate", true, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Hand> destroySwing = this.register(new Setting<>("DestroySwing", Hand.MAIN, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    // and this: public final Setting<Boolean> inhibit = this.register(new Setting<>("Inhibit", true, (v) -> menu.getValue() == Menu.PLACE && place.getValue()));
    public final Setting<Boolean> destroyPacket = this.register(new Setting<>("PacketDestroy", false, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Integer> packetLimit = this.register(new Setting<>("PacketLimit", 5, 1, 50, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));
    public final Setting<Integer> destroyLimitPerTick = this.register(new Setting<>("DestroyLimitPerTick", 3, 1, 10, (v) -> menu.getValue() == Menu.DESTROY && destroy.getValue()));

    // target options
    public final Setting<Priority> priority = this.register(new Setting<>("Priority", Priority.CLOSEST, (v) -> menu.getValue() == Menu.TARGET));
    public final Setting<Float> targetRange = this.register(new Setting<>("TargetRange", 7.0f, 1.0f, 15.0f, (v) -> menu.getValue() == Menu.TARGET));
    public final Setting<AntiFriendPop> antiFriendPop = this.register(new Setting<>("AntiFriendPop", AntiFriendPop.BOTH, (v) -> menu.getValue() == Menu.TARGET));

    // miscellaneous options
    public final Setting<Float> cooldown = this.register(new Setting<>("Cooldown", 250.0f, 0.0f, 1000.0f, (v) -> menu.getValue() == Menu.MISC));
    public final Setting<Boolean> antiSuicide = this.register(new Setting<>("AntiSuicide", true, (v) -> menu.getValue() == Menu.MISC));
    public final Setting<Boolean> autoRemoveCrystals = this.register(new Setting<>("CrystalRemove", false, (v) -> menu.getValue() == Menu.MISC));
    public final Setting<Boolean> stopOnMine = this.register(new Setting<>("StopOnMine", false, (v) -> menu.getValue() == Menu.MISC));
    public final Setting<Boolean> forceRotate = this.register(new Setting<>("ForceRotate", false, (v) -> menu.getValue() == Menu.MISC));
    public final Setting<Boolean> extraCalc = this.register(new Setting<>("ExtraCalc", true, (v) -> menu.getValue() == Menu.PLACE));

    // render options
    public final Setting<RenderType> renderType = this.register(new Setting<>("PlaceRenderType", RenderType.BOTH, (v) -> menu.getValue() == Menu.RENDER));
    public final Setting<ColorUtils.Color> placeColor = this.register(new Setting<>("PlaceColor", new ColorUtils.Color(255, 255, 255, 80), (v) -> menu.getValue() == Menu.RENDER && renderType.getValue() != RenderType.NONE));
    public final Setting<Boolean> renderDamage = this.register(new Setting<>("RenderDmg", true, (v) -> menu.getValue() == Menu.RENDER && renderType.getValue() != RenderType.NONE));

    private EntityPlayer target = null;
    private BlockPos currentPos = null;
    private float currentDamage = 0.0f;
    private int brokenCrystals = 0;
    private final Queue<CPacketUseEntity> destroyPackets = new ConcurrentLinkedDeque<>();
    private final Queue<BlockPos> queuedPositions = new ConcurrentLinkedDeque<>();

    private final Timer cooldownTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer destroyTimer = new Timer();
    private final Timer predictTimer = new Timer();


    public enum Menu {
        PLACE, DESTROY, TARGET, MISC, RENDER
    }

    public enum Hand {
        CORRECT(null),
        MAIN(EnumHand.MAIN_HAND),
        OFF(EnumHand.OFF_HAND),
        NONE(null);

        private EnumHand hand;
        Hand(EnumHand hand) {
            this.hand = hand;
        }

        public EnumHand getHand() {
            return hand;
        }
    }

    public enum Priority {
        CLOSEST, DAMAGE
    }

    public enum AntiFriendPop {
        NONE, PLACE, DESTROY, BOTH
    }

    public enum RenderType {
        NONE, FILLED, OUTLINE, BOTH
    }

    private static class Result {
        private final EntityPlayer target;
        private final BlockPos pos;
        private final float damage;

        public Result(EntityPlayer target, BlockPos pos, float damage) {
            this.target = target;
            this.pos = pos;
            this.damage = damage;
        }

        public EntityPlayer getTarget() {
            return target;
        }

        public BlockPos getPos() {
            return pos;
        }

        public float getDamage() {
            return damage;
        }
    }
}
