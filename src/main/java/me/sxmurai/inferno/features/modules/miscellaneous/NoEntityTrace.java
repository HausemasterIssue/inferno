package me.sxmurai.inferno.features.modules.miscellaneous;

import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import net.minecraft.item.ItemPickaxe;

@Module.Define(name = "NoEntityTrace", description = "Allows you to mine blocks through entities")
public class NoEntityTrace extends Module {
    public static NoEntityTrace INSTANCE;

    public final Setting<Boolean> pickaxe = new Setting<>("Pickaxe", true);
    public final Setting<Boolean> mining = new Setting<>("Mining", true);

    public NoEntityTrace() {
        INSTANCE = this;
    }

    public static boolean shouldBlock() {
        return (!INSTANCE.pickaxe.getValue() || mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) && (INSTANCE.mining.getValue() && mc.playerController.getIsHittingBlock());
    }
}
