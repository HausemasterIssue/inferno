package me.sxmurai.inferno.mixin;

import me.sxmurai.inferno.Inferno;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;

//@IFMLLoadingPlugin.Name("ForgeMixinLoader")
//@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE)
//@IFMLLoadingPlugin.MCVersion("1.12.2")
public class ForgeMixinLoader implements IFMLLoadingPlugin {
    public ForgeMixinLoader() {
        Inferno.LOGGER.info("Loading Inferno mixins...");
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        Mixins.addConfiguration("mixins.inferno.json");
        Inferno.LOGGER.info("Loaded mixins!");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> map) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
