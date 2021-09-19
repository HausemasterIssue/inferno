package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.EntityUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Tracers", description = "Draws lines to entities", category = Module.Category.RENDER)
public class Tracers extends Module {
    public final Setting<Float> distance = this.register(new Setting<>("Distance", 100.0f, 1.0f, 300.0f));
    public final Setting<Float> width = this.register(new Setting<>("Width", 1.0f, 0.1f, 5.0f));
    public final Setting<Target> target = this.register(new Setting<>("Target", Target.FEET));
    public final Setting<Boolean> smooth = this.register(new Setting<>("Smooth", true));
    public final Setting<Boolean> stem = this.register(new Setting<>("Stem", false));
    public final Setting<Boolean> invisible = this.register(new Setting<>("Invisible", true));
    public final Setting<Boolean> players = this.register(new Setting<>("Players", true));
    public final Setting<Boolean> friends = this.register(new Setting<>("Friends", true, (v) -> players.getValue()));
    public final Setting<Boolean> passive = this.register(new Setting<>("Passive", false));
    public final Setting<Boolean> hostile = this.register(new Setting<>("Hostile", false));

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck() && event.getType() == RenderEvent.Type.WORLD) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!EntityUtils.isLiving(entity) || entity == mc.player || mc.player.getDistance(entity) > distance.getValue()) {
                    continue;
                }

                if (invisible.getValue() && EntityUtils.isInvisible((EntityLivingBase) entity)) {
                    continue;
                }

                if (EntityUtils.isPlayer(entity)) {
                    if (!players.getValue()) {
                        continue;
                    }

                    if (!friends.getValue() && Inferno.friendManager.isFriend((EntityPlayer) entity)) {
                        continue;
                    }
                }

                if (!passive.getValue() && EntityUtils.isPassive(entity)) {
                    continue;
                }

                if (!hostile.getValue() && EntityUtils.isHostile(entity)) {
                    continue;
                }

                float r, g, b;
                if (entity instanceof EntityPlayer && Inferno.friendManager.isFriend((EntityPlayer) entity)) {
                    r = 0.0f;
                    g = 0.5f;
                    b = 1.0f;
                } else  {
                    float distance = mc.player.getDistance(entity) / 20.0f;
                    r = 2.0f - distance;
                    g = distance;
                    b = 0.0f;
                }

                GlStateManager.pushMatrix();
                RenderUtils.drawTracer(entity, target.getValue().run(entity), width.getValue(), r, g, b, 1.0f, smooth.getValue(), stem.getValue());
                GlStateManager.popMatrix();
            }
        }
    }

    public enum Target {
        FEET((v) -> 0.0f),
        BODY((v) -> v.height / 2.0f),
        HEAD((v) -> v.height);

        private final Run runnable;
        Target(Run runnable) {
            this.runnable = runnable;
        }

        public float run(Entity entity) {
            return this.runnable.getStartHeight(entity);
        }
    }

    @FunctionalInterface
    private interface Run {
        float getStartHeight(Entity entity);
    }
}
