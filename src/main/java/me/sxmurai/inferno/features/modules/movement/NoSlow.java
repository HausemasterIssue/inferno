package me.sxmurai.inferno.features.modules.movement;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.PacketEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
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

    public final Setting<Boolean> items = this.register(new Setting<>("Items", true));
    public final Setting<Boolean> soulsand = this.register(new Setting<>("SoulSand", false));
    public final Setting<Boolean> slime = this.register(new Setting<>("Slime", false));
    public final Setting<Boolean> strict = this.register(new Setting<>("Strict", false));
    public final Setting<Boolean> sneak = this.register(new Setting<>("Sneak", true));
    public final Setting<Boolean> guiMove = this.register(new Setting<>("GuiMove", false));

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
            if (this.sneak.getValue() && !mc.player.isHandActive() && this.items.getValue() && this.sneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.sneaking = false;
            }

            if (guiMove.getValue() && mc.currentScreen != null) {
                for (KeyBinding bind : KEYS) {
                    bind.pressed = Keyboard.isKeyDown(bind.keyCode);
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                    mc.player.rotationPitch -= 2.0f;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                    mc.player.rotationPitch += 2.0f;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                    mc.player.rotationYaw += 2.0f;
                } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                    mc.player.rotationYaw -= 2.0f;
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
