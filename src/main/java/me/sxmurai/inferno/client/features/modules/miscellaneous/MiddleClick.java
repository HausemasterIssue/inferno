package me.sxmurai.inferno.client.features.modules.miscellaneous;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

@Module.Define(name = "MiddleClick", description = "Does various things upon a middle click")
public class MiddleClick extends Module {
    public final Value<OverPlayer> overPlayer = new Value<>("OverPlayer", OverPlayer.NOTHING);
    public final Value<Boolean> unfriend = new Value<>("Unfriend", true, (v) -> overPlayer.getValue() == OverPlayer.FRIEND);
    public final Value<Boolean> notify = new Value<>("Notify", true, (v) -> overPlayer.getValue() == OverPlayer.FRIEND);
    public final Value<Boolean> pearl = new Value<>("Pearl", false);
    public final Value<Boolean> offhand = new Value<>("Offhand", true, (v) -> pearl.getValue());
    public final Value<Boolean> silentSwitch = new Value<>("SilentSwitch", false, (v) -> pearl.getValue());

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
