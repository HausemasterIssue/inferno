package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.api.events.render.RenderEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import me.sxmurai.inferno.api.utils.EntityUtils;
import me.sxmurai.inferno.api.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "Tracers", description = "Draws lines to entities", category = Module.Category.RENDER)
public class Tracers extends Module {
    public final Value<Float> distance = new Value<>("Distance", 100.0f, 1.0f, 300.0f);
    public final Value<Float> width = new Value<>("Width", 1.0f, 0.1f, 5.0f);
    public final Value<Target> target = new Value<>("Target", Target.FEET);
    public final Value<Boolean> smooth = new Value<>("Smooth", true);
    public final Value<Boolean> stem = new Value<>("Stem", false);
    public final Value<Boolean> invisible = new Value<>("Invisible", true);
    public final Value<Boolean> players = new Value<>("Players", true);
    public final Value<Boolean> friends = new Value<>("Friends", true, (v) -> players.getValue());
    public final Value<Boolean> passive = new Value<>("Passive", false);
    public final Value<Boolean> hostile = new Value<>("Hostile", false);

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
