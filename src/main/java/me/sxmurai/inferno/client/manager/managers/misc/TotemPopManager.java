package me.sxmurai.inferno.client.manager.managers.misc;

import io.netty.util.internal.ConcurrentSet;
import me.sxmurai.inferno.api.events.entity.DeathEvent;
import me.sxmurai.inferno.api.events.entity.TotemPopEvent;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.events.network.ConnectionEvent;
import me.sxmurai.inferno.api.events.network.SelfConnectionEvent;
import me.sxmurai.inferno.client.modules.client.Notifications;
import me.sxmurai.inferno.client.manager.Manager;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.commands.text.ChatColor;
import me.sxmurai.inferno.client.manager.managers.commands.text.TextBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TotemPopManager extends Manager {
    private final Map<EntityPlayer, Integer> pops = new ConcurrentHashMap<>();
    private final Set<EntityPlayer> toAnnounce = new ConcurrentSet<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (Notifications.INSTANCE.isToggled() && Notifications.INSTANCE.totemPops.getValue()) {
            if (!this.toAnnounce.isEmpty()) {
                for (EntityPlayer player : toAnnounce) {
                    Integer popCount = this.pops.getOrDefault(player, -1);
                    if (popCount != -1) {
                        Command.send(new TextBuilder()
                                .append(ChatColor.Red, player.getName())
                                .append(" ")
                                .append(ChatColor.Red, "has popped")
                                .append(" ")
                                .append(ChatColor.Green, String.valueOf(popCount))
                                .append(" ")
                                .append(ChatColor.Red, "totem")
                                .append(ChatColor.Red, popCount > 1 ? "s." : ".")
                        );
                    }

                    this.toAnnounce.remove(player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        if (Notifications.INSTANCE.isToggled() && Notifications.INSTANCE.totemPops.getValue()) {
            Integer popCount = this.pops.getOrDefault(event.getPlayer(), -1);
            if (popCount == -1) {
                return;
            }

            Command.send(new TextBuilder()
                    .append(ChatColor.Red, event.getPlayer().getName())
                    .append(" ")
                    .append(ChatColor.Red, "died after popping")
                    .append(" ")
                    .append(ChatColor.Green, String.valueOf(popCount))
                    .append(" ")
                    .append(ChatColor.Red, "totem")
                    .append(ChatColor.Red, popCount > 1 ? "s." : ".")
            );

            this.pops.remove(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if (Notifications.INSTANCE.isToggled() && Notifications.INSTANCE.totemPops.getValue()) {
            if (event.getPlayer() != mc.player) {
                this.pops.merge(event.getPlayer(), 1, Integer::sum);
                this.toAnnounce.add(event.getPlayer());
            }
        }
    }

    @SubscribeEvent
    public void onSelfLogout(SelfConnectionEvent event) {
        if (event.getType() == SelfConnectionEvent.Type.DISCONNECT) {
            this.pops.clear();
            this.toAnnounce.clear();
        }
    }

    @SubscribeEvent
    public void onLogout(ConnectionEvent event) {
        if (event.getType() == ConnectionEvent.Type.DISCONNECT && this.pops.containsKey(event.getPlayer())) {
            this.pops.remove(event.getPlayer());
            this.toAnnounce.remove(event.getPlayer());
        }
    }
}
