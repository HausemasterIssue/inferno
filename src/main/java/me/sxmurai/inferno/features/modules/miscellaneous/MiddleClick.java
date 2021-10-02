package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.Command;
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
    public final Setting<OverPlayer> overPlayer = new Setting<>("OverPlayer", OverPlayer.NOTHING);
    public final Setting<Boolean> unfriend = new Setting<>("Unfriend", true, (v) -> overPlayer.getValue() == OverPlayer.FRIEND);
    public final Setting<Boolean> notify = new Setting<>("Notify", true, (v) -> overPlayer.getValue() == OverPlayer.FRIEND);
    public final Setting<Boolean> pearl = new Setting<>("Pearl", false);
    public final Setting<Boolean> offhand = new Setting<>("Offhand", true, (v) -> pearl.getValue());
    public final Setting<Boolean> silentSwitch = new Setting<>("SilentSwitch", false, (v) -> pearl.getValue());

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent event) {
        if (!Module.fullNullCheck() && Mouse.getEventButton() == 2 && !Mouse.getEventButtonState() && mc.objectMouseOver != null) {
            if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;

                if (this.overPlayer.getValue() == OverPlayer.FRIEND) {
                    if (Inferno.friendManager.isFriend(player)) {
                        if (!unfriend.getValue()) {
                            return;
                        }

                        Inferno.friendManager.removeFriend(player);
                        if (notify.getValue()) {
                            Command.send("Unfriended " + player.getName());
                        }
                    } else {
                        Inferno.friendManager.addFriend(player);
                        if (notify.getValue()) {
                            Command.send("Friended " + player.getName());
                        }
                    }
                } else if (this.overPlayer.getValue() == OverPlayer.DUEL) {
                    mc.player.sendChatMessage("/duel " + player.getName());
                }

                return;
            }

            if (pearl.getValue()) {
                int slot = InventoryUtils.getHotbarItemSlot(Items.ENDER_PEARL, offhand.getValue());
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
        }
    }

    public enum OverPlayer {
        NOTHING, FRIEND, DUEL
    }
}
