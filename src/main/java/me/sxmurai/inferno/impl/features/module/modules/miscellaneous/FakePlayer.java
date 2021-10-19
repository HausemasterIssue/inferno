package me.sxmurai.inferno.impl.features.module.modules.miscellaneous;

import me.sxmurai.inferno.impl.features.module.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.GameType;

@Module.Define(name = "FakePlayer")
@Module.Info(description = "Spawns in a fake player")
public class FakePlayer extends Module {
    private EntityOtherPlayerMP fake = null;

    @Override
    protected void onActivated() {
        if (!fullNullCheck()) {
            this.toggle();
            return;
        }

        this.fake = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        this.fake.copyLocationAndAnglesFrom(mc.player);
        this.fake.inventory.copyInventory(mc.player.inventory);
        this.fake.setHealth(20.0f);
        this.fake.setGameType(GameType.SURVIVAL);

        mc.world.spawnEntity(this.fake);
    }

    @Override
    protected void onDeactivated() {
        if (fullNullCheck()) {
            mc.world.removeEntity(this.fake);
            mc.world.removeEntityDangerously(this.fake);
        }

        this.fake = null;
    }
}
