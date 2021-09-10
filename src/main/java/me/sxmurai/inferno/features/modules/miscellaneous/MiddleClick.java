package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

@Module.Define(name = "MiddleClick", description = "Does various things upon a middle click")
public class MiddleClick extends Module {
    public final Setting<OverPlayer> overPlayer = this.register(new Setting<>("OverPlayer", OverPlayer.NOTHING));
    public final Setting<Boolean> unfriend = this.register(new Setting<>("Unfriend", true, (v) -> overPlayer.getValue() == OverPlayer.FRIEND));
    public final Setting<Boolean> pearl = this.register(new Setting<>("Pearl", false));
    public final Setting<Boolean> offhand = this.register(new Setting<>("Offhand", true, (v) -> pearl.getValue()));
    public final Setting<Boolean> silentSwitch = this.register(new Setting<>("SilentSwitch", false, (v) -> pearl.getValue()));

    @SubscribeEvent
    public void onMouseInput(InputEvent.KeyInputEvent event) {
        if (!Module.fullNullCheck() && !Mouse.getEventButtonState()) {
            if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS) {
                if (pearl.getValue()) {
                    int slot = InventoryUtils.getInventoryItemSlot(Items.ENDER_PEARL, offhand.getValue());
                    if (slot == -1) {
                        return;
                    }

                    int oldSlot = mc.player.inventory.currentItem;
                    if (slot != 45) {
                        InventoryUtils.switchTo(slot, silentSwitch.getValue());
                    }

                    mc.playerController.processRightClick(mc.player, mc.world, slot == 45 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

                    if (slot != 45) {
                        InventoryUtils.switchTo(oldSlot, silentSwitch.getValue());
                    }
                }
            } else if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                if (overPlayer.getValue() == OverPlayer.NOTHING) {
                    return;
                }

                EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;

                if (overPlayer.getValue() == OverPlayer.FRIEND) {
                    if (Inferno.friendManager.isFriend(player)) {
                        if (!unfriend.getValue()) {
                            return;
                        }

                        Inferno.friendManager.removeFriend(player);
                    } else {
                        Inferno.friendManager.addFriend(player);
                    }
                } else if (overPlayer.getValue() == OverPlayer.DUEL) {
                    mc.player.sendChatMessage("/duel " + player.getName());
                }
            }
        }
    }

    public enum OverPlayer {
        NOTHING, FRIEND, DUEL
    }
}
