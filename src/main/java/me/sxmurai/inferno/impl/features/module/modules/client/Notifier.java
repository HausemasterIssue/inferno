package me.sxmurai.inferno.impl.features.module.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.api.event.entity.EntityRemoveEvent;
import me.sxmurai.inferno.api.event.entity.EntitySpawnEvent;
import me.sxmurai.inferno.api.event.inferno.ModuleToggledEvent;
import me.sxmurai.inferno.api.event.network.PacketEvent;
import me.sxmurai.inferno.impl.features.command.Command;
import me.sxmurai.inferno.impl.features.module.Module;
import me.sxmurai.inferno.impl.option.Option;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

@Module.Define(name = "Notifier", category = Module.Category.Client)
@Module.Info(description = "Notifies you when things happen")
public class Notifier extends Module {
    public static Notifier INSTANCE;

    public final Option<Boolean> modules = new Option<>("Modules", true);
    public static final Option<Boolean> totems = new Option<>("Totems", false);
    public final Option<Boolean> pearl = new Option<>("Pearl", false);
    public final Option<Boolean> chorus = new Option<>("Chorus", false);
    public final Option<Boolean> visualRange = new Option<>("VisualRange", false);
    public final Option<Boolean> friends = new Option<>("Friends", false, this.visualRange::getValue);

    public Notifier() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onModuleToggled(ModuleToggledEvent event) {
        if (this.modules.getValue()) {
            Module module = event.getModule();
            Inferno.notificationManager.notify(
                    module.hashCode(),
                    module.getName() + " has been toggled "
                            + (module.isOn() ? (ChatFormatting.GREEN + "on") : (ChatFormatting.RED + "off")) + ChatFormatting.RESET + "."
            );
        }
    }

    @SubscribeEvent
    public void onEntitySpawnEvent(EntitySpawnEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            if (this.visualRange.getValue()) {
                Inferno.notificationManager.notifyNoEdit(event.getEntity().getName() + " has entered your visual range.");
            }
        } else if (event.getEntity() instanceof EntityEnderPearl) {
            // @todo: this for some reason also notifies when the pearl hits the ground. i dont know why it does this
            if (this.pearl.getValue()) {
                mc.world.playerEntities.stream()
                        // .filter((player) -> player != null)
                        .min(Comparator.comparingDouble((v) -> event.getEntity().getDistance(v)))
                        .ifPresent(player -> Inferno.notificationManager.notifyNoEdit(player.getName() + " has thrown a pearl!"));
            }
        }
    }

    @SubscribeEvent
    public void onEntityRemove(EntityRemoveEvent event) {
        if (event.getEntity() instanceof EntityPlayer && this.visualRange.getValue()) {
            Inferno.notificationManager.notifyNoEdit(event.getEntity().getName() + " has left your visual range.");
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT && this.chorus.getValue()) {
                double x = packet.getX();
                double y = packet.getY();
                double z = packet.getZ();
                Command.send("A player ate a chorus fruit at " + ChatFormatting.RED + "X: " + x + ", Y: " + y + ", Z: " + z + ChatFormatting.RESET);
            }
        }
    }
}
