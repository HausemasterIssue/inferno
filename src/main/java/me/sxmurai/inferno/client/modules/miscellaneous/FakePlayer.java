package me.sxmurai.inferno.client.modules.miscellaneous;

import com.mojang.authlib.GameProfile;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

@Module.Define(name = "FakePlayer", description = "Spawns in a dummy player to configure on")
public class FakePlayer extends Module {
    // @todo settings

    private EntityOtherPlayerMP fakePlayer;

    @Override
    protected void onActivated() {
        if (Module.fullNullCheck()) {
            toggle();
            return;
        }

        fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.randomUUID(), "Fit"));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.setEntityId(-694201337);

        mc.world.spawnEntity(fakePlayer);
    }

    @Override
    protected void onDeactivated() {
        if (!Module.fullNullCheck()) {
            mc.world.removeEntity(fakePlayer);
            mc.world.removeEntityDangerously(fakePlayer);
        }

        fakePlayer = null;
    }
}
