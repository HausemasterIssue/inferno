package me.sxmurai.inferno.impl.features.module.modules.movement;

import me.sxmurai.inferno.api.event.network.PacketEvent;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Module.Define(name = "NoSlow", category = Module.Category.Movement)
@Module.Info(description = "Stops the slowdown effect")
public class NoSlow extends Module {
    public static NoSlow INSTANCE;
    private static final KeyBinding[] MOVEMENT_KEYS = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindSneak,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
    };

    public final Option<Boolean> ncpStrict = new Option<>("NCPStrict", true);
    public final Option<Boolean> sneak = new Option<>("Sneak", false);

    public final Option<Boolean> items = new Option<>("Items", true);
    public final Option<Boolean> guiMove = new Option<>("GuiMove", true);
    public static final Option<Boolean> soulSand = new Option<>("Soulsand", false);
    public static final Option<Boolean> slime = new Option<>("Slime", false);

    private boolean sneaking = false;

    public NoSlow() {
        INSTANCE = this;
    }

    @Override
    protected void onDeactivated() {
        if (fullNullCheck() && this.sneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }

        this.sneaking = false;
    }

    @Override
    public void onUpdate() {
        if (this.guiMove.getValue() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            mc.currentScreen.allowUserInput = true;

            for (KeyBinding binding : NoSlow.MOVEMENT_KEYS) {
                KeyBinding.setKeyBindState(binding.keyCode, Keyboard.isKeyDown(binding.keyCode));
            }
        }

        if (this.items.getValue() && !mc.player.isHandActive() && this.sneak.getValue() && this.sneaking) {
            this.sneaking = false;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (fullNullCheck() && event.getPacket() instanceof CPacketPlayer && this.ncpStrict.getValue() && this.shouldDoItemNoSlow()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, mc.player.getPosition(), EnumFacing.DOWN));
        }
    }

    @SubscribeEvent
    public void onItemUse(LivingEntityUseItemEvent event) {
        if (this.shouldDoItemNoSlow() && this.sneak.getValue() && !this.sneaking) {
            this.sneaking = true;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event) {
        if (this.shouldDoItemNoSlow()) {
            event.getMovementInput().moveForward *= 5.0f;
            event.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    private boolean shouldDoItemNoSlow() {
        return this.items.getValue() && mc.player.isHandActive() && !mc.player.isRiding();
    }
}
