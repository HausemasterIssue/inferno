package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.events.render.RenderModelEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@Module.Define(name = "CrystalModifier", description = "Modifies how crystals look", category = Module.Category.RENDER)
public class CrystalModifier extends Module {
    public static CrystalModifier INSTANCE;

    public final Setting<Boolean> glint = this.register(new Setting<>("Glint", false));
    public final Setting<Boolean> wireframe = this.register(new Setting<>("Wireframe", false));
    public final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f, (v) -> wireframe.getValue()));
    public final Setting<Boolean> throughWalls = this.register(new Setting<>("ThroughWalls", false));
    public final Setting<Float> scale = this.register(new Setting<>("Scale", 1.0f, 0.1, 10.0f));
    public final Setting<Boolean> colored = this.register(new Setting<>("Colored", false));
    public final Setting<ColorUtils.Color> color = this.register(new Setting<>("Color", new ColorUtils.Color(164, 66, 245, 135), (v) -> colored.getValue()));

    public CrystalModifier() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderModel(RenderModelEvent event) {
        if (!Module.fullNullCheck() && event.getEntity() instanceof EntityEnderCrystal && wireframe.getValue()) {
            boolean fancyGraphics = mc.gameSettings.fancyGraphics;
            mc.gameSettings.fancyGraphics = false;

            float gamma = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 10000.0f;

            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glPolygonMode(1032, 6913);
            GL11.glDisable(3553);
            GL11.glDisable(2896);

            if (throughWalls.getValue()) {
                GL11.glDisable(2929);
            }

            GL11.glEnable(2848);
            GL11.glEnable(3042);

            ColorUtils.Color c = color.getValue();

            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(c.red / 255.0f, c.green / 255.0f, c.blue / 255.0f, c.alpha / 255.0f);
            GlStateManager.glLineWidth(lineWidth.getValue());

            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());

            if (throughWalls.getValue()) {
                GL11.glEnable(2929);
            }

            GL11.glPopAttrib();
            GL11.glPopMatrix();

            mc.gameSettings.gammaSetting = gamma;
            mc.gameSettings.fancyGraphics = fancyGraphics;
        }
    }
}
