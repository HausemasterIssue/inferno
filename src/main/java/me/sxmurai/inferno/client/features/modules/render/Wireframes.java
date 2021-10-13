package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.api.events.render.RenderModelEvent;
import me.sxmurai.inferno.api.utils.ColorUtils;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@Module.Define(name = "Wireframes", description = "Draws lines around each part of an entities model", category = Module.Category.RENDER)
public class Wireframes extends Module {
    public final Value<Boolean> self = new Value<>("Self", true);
    public final Value<Float> distance = new Value<>("Distance", 50.0f, 1.0f, 300.0f);
    public final Value<Float> width = new Value<>("Width", 0.5f, 0.1f, 5.0f);
    public final Value<Boolean> throughWalls = new Value<>("ThroughWalls", true);
    public final Value<ColorUtils.Color> color = new Value<>("Color", new ColorUtils.Color(255, 255, 255));

    @SubscribeEvent
    public void onModelRender(RenderModelEvent event) {
        if (!Module.fullNullCheck() && event.getEntity() instanceof EntityLivingBase) {
            if (!self.getValue() && event.getEntity() == mc.player || mc.player.getDistance(event.getEntity()) > distance.getValue()) {
                return;
            }

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
            GlStateManager.glLineWidth(width.getValue());

            event.getModelBase().render(event.getEntity(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScale());

            if (throughWalls.getValue()) {
                GL11.glEnable(2929);
            }

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
