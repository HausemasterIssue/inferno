package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.HoleManager;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Module.Define(name = "HoleESP", description = "Shows where safe holes are", category = Module.Category.RENDER)
public class HoleESP extends Module {
    public final Setting<VoidESP.Mode> mode = this.register(new Setting<>("Mode", VoidESP.Mode.FILLED));
    public final Setting<Boolean> safe = this.register(new Setting<>("Safe", true));
    public final Setting<ColorUtils.Color> safeColor = this.register(new Setting<>("SafeColor", new ColorUtils.Color(0, 255, 0, 80), (v) -> safe.getValue()));
    public final Setting<Boolean> unsafe = this.register(new Setting<>("Unsafe", true));
    public final Setting<ColorUtils.Color> unsafeColor = this.register(new Setting<>("UnsafeColor", new ColorUtils.Color(0, 255, 0, 80), (v) -> unsafe.getValue()));
    public final Setting<Float> height = this.register(new Setting<>("Height", 1.0f, -1.0f, 2.0f, (v) -> mode.getValue() != VoidESP.Mode.FLAT && mode.getValue() != VoidESP.Mode.FLAT_OUTLINE));
    public final Setting<Float> width = this.register(new Setting<>("Width", 1.0f, 0.1f, 5.0f, (v) -> mode.getValue() == VoidESP.Mode.FLAT_OUTLINE || mode.getValue() == VoidESP.Mode.FILLED_OUTLINE || mode.getValue() == VoidESP.Mode.OUTLINE));

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck() && !Inferno.holeManager.getHoles().isEmpty()) {
            for (HoleManager.Hole hole : Inferno.holeManager.getHoles()) {
                if ((hole.isSafe() && !this.safe.getValue()) || (!hole.isSafe() && !this.unsafe.getValue())) {
                    continue;
                }

                AxisAlignedBB box = new AxisAlignedBB(hole.getPos());
                ColorUtils.Color c = hole.isSafe() ? this.safeColor.getValue() : this.unsafeColor.getValue();
                int colour = ColorUtils.toRGBA(c.red, c.green, c.blue, c.alpha);

                switch (this.mode.getValue()) {
                    case FILLED: {
                        RenderUtils.drawFilledBox(box.setMaxY(this.height.getValue()).offset(RenderUtils.screen()), colour);
                        break;
                    }

                    case OUTLINE: {
                        RenderUtils.drawOutlinedBox(box.setMaxY(this.height.getValue()).offset(RenderUtils.screen()), this.width.getValue(), colour);
                        break;
                    }

                    case FILLED_OUTLINE: {
                        RenderUtils.drawFilledBox(box.setMaxY(this.height.getValue()).offset(RenderUtils.screen()), colour);
                        RenderUtils.drawOutlinedBox(box.setMaxY(this.height.getValue()).offset(RenderUtils.screen()), this.width.getValue(), colour);
                        break;
                    }

                    case FLAT: {
                        RenderUtils.drawFilledBox(box.setMaxY(box.minY).offset(RenderUtils.screen()), colour);
                        RenderUtils.drawOutlinedBox(box.setMaxY(box.minY).offset(RenderUtils.screen()), this.width.getValue(), colour);
                        break;
                    }

                    case FLAT_OUTLINE: {
                        RenderUtils.drawOutlinedBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ), this.width.getValue(), colour);
                        break;
                    }
                }
            }
        }
    }

    public enum Mode {
        FILLED, OUTLINE, FILLED_OUTLINE, FLAT, FLAT_OUTLINE
    }
}
