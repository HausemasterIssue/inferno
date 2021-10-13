package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "NoSlow", description = "Stops you from being slowed down", category = Module.Category.MOVEMENT)
public class NoSlow extends Module {
    private static final KeyBinding[] KEYS = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindSneak,
            mc.gameSettings.keyBindJump
    };

    public static NoSlow INSTANCE;

    public final Value<Boolean> items = new Value<>("Items", true);
    public final Value<Boolean> soulsand = new Value<>("SoulSand", false);
    public final Value<Boolean> slime = new Value<>("Slime", false);
    public final Value<Boolean> strict = new Value<>("Strict", false);
    public final Value<Boolean> sneak = new Value<>("Sneak", true);
    public final Value<Boolean> guiMove = new Value<>("GuiMove", false);
    public final Value<Float> lookSpeed = new Value<>("LookSpeed", 3.0f, 0.1f, 15.0f, (v) -> guiMove.getValue());

    private boolean sneaking = false;

    public NoSlow() {
        INSTANCE = this;
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck() && this.sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.sneaking = false;
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (items.getValue() && mc.player.isHandActive()) {
            event.getMovementInput().moveForward *= 5.0f;
            event.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!Module.fullNullCheck()) {
            if (sneak.getValue() && !mc.player.isHandActive() && items.getValue() && this.sneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }

            if (guiMove.getValue() && mc.currentScreen != null) {
                for (KeyBinding bind : KEYS) {
                    bind.pressed = Keyboard.isKeyDown(bind.keyCode);
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    mc.player.rotationPitch -= this.lookSpeed.getValue();
                } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    mc.player.rotationPitch += this.lookSpeed.getValue();
                } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    mc.player.rotationYaw += this.lookSpeed.getValue();
                } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    mc.player.rotationYaw -= this.lookSpeed.getValue();
                }

                mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch, -90.0f, 90.0f);
            }
        }
    }

    @SubscribeEvent
    public void onUseItem(LivingEntityUseItemEvent event) {
        if (items.getValue() && sneak.getValue() && !this.sneaking) {
            this.sneaking = true;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (!Module.fullNullCheck() && event.getPacket() instanceof CPacketPlayer && strict.getValue() && mc.player.isHandActive()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.player.getPositionVector()), EnumFacing.DOWN));
        }
    }
}