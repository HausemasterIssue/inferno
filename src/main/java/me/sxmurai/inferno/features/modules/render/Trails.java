package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.Pair;
import me.sxmurai.inferno.utils.RenderUtils;
import me.sxmurai.inferno.utils.Timer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// @todo code is kinda shit, refactor it a bit?
@Module.Define(name = "Trails", description = "Draws trails after projectiles to see where they go", category = Module.Category.RENDER)
public class Trails extends Module {
    public final Setting<ColorUtils.Color> color = this.register(new Setting<>("Color", new ColorUtils.Color(255, 255, 255)));
    public final Setting<Boolean> smooth = this.register(new Setting<>("Smooth", true));
    public final Setting<Boolean> onlyOwn = this.register(new Setting<>("OnlyOwn", false));
    public final Setting<RemoveIf> removeIf = this.register(new Setting<>("RemoveIf", RemoveIf.ONGROUND));
    public final Setting<Float> delay = this.register(new Setting<>("Delay", 1.0f, 0.0f, 10.0f, (v) -> removeIf.getValue() == RemoveIf.DELAY));
    public final Setting<Float> lineWidth = this.register(new Setting<>("LineWidth", 1.0f, 0.1f, 5.0f));
    public final Setting<Boolean> arrows = this.register(new Setting<>("Arrows", true));
    public final Setting<Boolean> pearls = this.register(new Setting<>("Pearls", true));

    private final Map<Entity, CopyOnWriteArrayList<Pair<BlockPos, BlockPos>>> projectiles = new ConcurrentHashMap<>();
    private final Map<Entity, Timer> timers = new ConcurrentHashMap<>();

    @Override
    protected void onDeactivated() {
        projectiles.clear();
        timers.clear();
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck() && event.getType() == RenderEvent.Type.WORLD) {
            for (Map.Entry<Entity, CopyOnWriteArrayList<Pair<BlockPos, BlockPos>>> entry : projectiles.entrySet()) {
                Entity entity = mc.world.getEntityByID(entry.getKey().entityId);
                if (entity == null) {
                    projectiles.remove(entry.getKey());
                    timers.remove(entry.getKey());
                    continue;
                }

                if (entity.onGround) {
                    if (removeIf.getValue() == RemoveIf.ONGROUND) {
                        projectiles.remove(entry.getKey());
                        continue;
                    } else if (removeIf.getValue() == RemoveIf.DELAY) {
                        Timer timer = timers.computeIfAbsent(entry.getKey(), (v) -> new Timer().reset());
                        if (timer.passedS(delay.getValue().doubleValue())) {
                            projectiles.remove(entry.getKey());
                            timers.remove(entry.getKey());
                            continue;
                        }
                    }
                }

                if (!entry.getValue().isEmpty()) {
                    for (Pair<BlockPos, BlockPos> pair : entry.getValue()) {
                        ColorUtils.Color c = color.getValue();
                        Vec3d start = RenderUtils.toScreen(RenderUtils.interpolateVec(new Vec3d(pair.getKey()), new Vec3d(pair.getKey())));
                        Vec3d end = RenderUtils.toScreen(RenderUtils.interpolateVec(new Vec3d(pair.getValue()), new Vec3d(pair.getValue())));

                        GlStateManager.pushMatrix();
                        RenderUtils.drawLine(start.x, start.y, start.z, end.x, end.y, end.z, lineWidth.getValue(), smooth.getValue(), c.red / 255.0f, c.green / 255.0f, c.blue / 255.0f, c.alpha / 255.0f);
                        GlStateManager.popMatrix();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!Module.fullNullCheck()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof IProjectile)) {
                    continue;
                }

                if (entity instanceof EntityEnderPearl && !pearls.getValue()) {
                    continue;
                }

                if (entity instanceof EntityArrow && !arrows.getValue()) {
                    continue;
                }

                if (onlyOwn.getValue() && entity instanceof EntityThrowable && ((EntityThrowable) entity).getThrower() != mc.player) {
                    continue;
                }

                CopyOnWriteArrayList<Pair<BlockPos, BlockPos>> positions = projectiles.computeIfAbsent(entity, (v) -> new CopyOnWriteArrayList<>());

                BlockPos lastPosition;
                if (!positions.isEmpty()) {
                    lastPosition = positions.get(positions.size() - 1).getValue();
                } else {
                    lastPosition = new BlockPos(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
                }

                positions.add(new Pair<>(lastPosition, new BlockPos(entity.posX, entity.posY, entity.posZ)));
            }
        }
    }

    public enum RemoveIf {
        ONGROUND, DELAY
    }
}
