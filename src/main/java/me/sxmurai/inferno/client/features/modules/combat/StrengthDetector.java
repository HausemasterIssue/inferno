package me.sxmurai.inferno.client.features.modules.combat;

import io.netty.util.internal.ConcurrentSet;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.commands.Command;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import java.util.Set;

@Module.Define(name = "StrengthDetector", description = "Detects if someone has the strength status effect", category = Module.Category.COMBAT)
public class StrengthDetector extends Module {
    public final Value<Float> range = new Value<>("Range", 10.0f, 1.0f, 50.0f);
    public final Value<Boolean> removed = new Value<>("Removed", true);

    private final Set<EntityPlayer> players = new ConcurrentSet<>();

    @Override
    public void onTick() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player == null || mc.player.getDistance(player) > this.range.getValue()) {
                continue;
            }

            if (!this.players.contains(player) && player.isPotionActive(MobEffects.STRENGTH)) {
                Command.send(player.getName() + " has strength!");
                this.players.add(player);
            }
        }

        if (!this.players.isEmpty()) {
            for (EntityPlayer player : this.players) {
                if (!mc.world.playerEntities.contains(player)) {
                    this.players.remove(player);
                    continue;
                }

                if (!player.isPotionActive(MobEffects.STRENGTH)) {
                    this.players.remove(player);

                    if (this.removed.getValue()) {
                        Command.send(player.getName() + " no longer has strength!");
                    }
                }
            }
        }
    }
}
