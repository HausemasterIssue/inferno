package me.sxmurai.inferno.features.modules.combat;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.entity.EntitySpawnEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "AutoLog", description = "Becomes cringe and allows you to log out on practice cpvp servers", category = Module.Category.COMBAT)
public class AutoLog extends Module {
    public final Setting<Boolean> shutdown = this.register(new Setting<>("Shutdown", false));
    public final Setting<Boolean> toggle = this.register(new Setting<>("Toggle", true));
    public final Setting<Boolean> health = this.register(new Setting<>("Health", true));
    public final Setting<Float> healthAmount = this.register(new Setting<>("HealthAmount", 4.0f, 0.1f, 36.0f, (v) -> health.getValue()));
    public final Setting<Boolean> totems = this.register(new Setting<>("Totems", false));
    public final Setting<Float> totemAmount = this.register(new Setting<>("TotemAmount", 1, 0, 36, (v) -> totems.getValue()));
    public final Setting<Boolean> newPlayer = this.register(new Setting<>("NewPlayer", false));
    public final Setting<Boolean> ignoreFriends = this.register(new Setting<>("IgnoreFriends", true, (v) -> newPlayer.getValue()));

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
