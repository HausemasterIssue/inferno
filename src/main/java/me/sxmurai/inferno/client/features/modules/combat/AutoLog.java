package me.sxmurai.inferno.client.features.modules.combat;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.entity.EntitySpawnEvent;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AutoLog", description = "Becomes cringe and allows you to log out on practice cpvp servers", category = Module.Category.COMBAT)
public class AutoLog extends Module {
    public final Value<Boolean> shutdown = new Value<>("Shutdown", false);
    public final Value<Boolean> toggle = new Value<>("Toggle", true);
    public final Value<Boolean> health = new Value<>("Health", true);
    public final Value<Float> healthAmount = new Value<>("HealthAmount", 4.0f, 0.1f, 36.0f, (v) -> health.getValue());
    public final Value<Boolean> totems = new Value<>("Totems", false);
    public final Value<Integer> totemAmount = new Value<>("TotemAmount", 1, 0, 36, (v) -> totems.getValue());
    public final Value<Boolean> newPlayer = new Value<>("NewPlayer", false);
    public final Value<Boolean> ignoreFriends = new Value<>("IgnoreFriends", true, (v) -> newPlayer.getValue());

    @SubscribeEvent
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (this.newPlayer.getValue() && event.getEntity() instanceof EntityPlayer) {
            if (this.ignoreFriends.getValue() && Inferno.friendManager.isFriend((EntityPlayer) event.getEntity())) {
                return;
            }

            this.log("New player has entered your visual range!");
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.totems.getValue() && InventoryUtils.getCount(Items.TOTEM_OF_UNDYING, true, true) < this.totemAmount.getValue()) {
            this.log("You had less than " + this.totemAmount.getValue() + " totems!");
            return;
        }

        if (this.health.getValue() && EntityUtils.getHealth(mc.player) < this.healthAmount.getValue()) {
            this.log("Your health was below " + this.healthAmount.getValue() + "!");
            return;
        }
    }

    private void log(String reason) {
        if (this.shutdown.getValue()) {
            mc.shutdown();
        } else {
            mc.player.connection.getNetworkManager().closeChannel(new TextBuilder.ChatMessage(
                    new TextBuilder()
                            .append(ChatColor.Red, "[AutoLog]")
                            .append(" ")
                            .append(ChatColor.Dark_Gray, "\u00BB")
                            .append(" ")
                            .append(reason)
                            .build()
            ));
        }

        if (this.toggle.getValue()) {
            this.toggle();
        }
    }
}
