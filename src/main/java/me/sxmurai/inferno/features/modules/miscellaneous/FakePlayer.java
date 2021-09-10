package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;

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

        fakePlayer = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
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
