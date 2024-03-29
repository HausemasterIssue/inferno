package me.sxmurai.inferno.managers;

import me.sxmurai.inferno.events.entity.DeathEvent;
import me.sxmurai.inferno.events.entity.TotemPopEvent;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.network.ConnectionEvent;
import me.sxmurai.inferno.events.network.SelfConnectionEvent;
import me.sxmurai.inferno.features.Feature;
import me.sxmurai.inferno.features.modules.combat.TotemPopNotifier;
import me.sxmurai.inferno.managers.commands.Command;
import me.sxmurai.inferno.managers.commands.text.ChatColor;
import me.sxmurai.inferno.managers.commands.text.TextBuilder;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TotemPopManager extends Feature {
    private final Map<EntityPlayer, Integer> pops = new ConcurrentHashMap<>();
    private final Set<EntityPlayer> toAnnounce = new HashSet<>();
    private final Timer timer = new Timer();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (TotemPopNotifier.INSTANCE.isToggled() && this.timer.passedS(TotemPopNotifier.INSTANCE.delay.getValue().doubleValue())) {
            this.timer.reset();

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
        Integer popCount = pops.getOrDefault(event.getPlayer(), -1);
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

        pops.remove(event.getPlayer());
    }

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        if (event.getPlayer() != mc.player) {
            this.pops.merge(event.getPlayer(), 1, Integer::sum);
            this.toAnnounce.add(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void onSelfLogout(SelfConnectionEvent event) {
        if (event.getType() == SelfConnectionEvent.Type.DISCONNECT) {
            this.pops.clear();
            this.toAnnounce.clear();
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onLogout(ConnectionEvent event) {
        if (event.getType() == ConnectionEvent.Type.DISCONNECT && this.pops.containsKey(event.getPlayer()) && TotemPopNotifier.INSTANCE.clearOnLog.getValue()) {
            this.pops.remove(event.getPlayer());
            this.toAnnounce.remove(event.getPlayer());
        }
    }
}
