package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.utils.BlockUtil;
import me.sxmurai.inferno.utils.ColorUtils;
import me.sxmurai.inferno.utils.RenderUtils;
import me.sxmurai.inferno.utils.timing.TickTimer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Module.Define(name = "VoidESP", description = "Shows where void holes are", category = Module.Category.RENDER)
public class VoidESP extends Module {
    public final Setting<Float> range = this.register(new Setting<>("Range", 10.0f, 1.0f, 50.0f));
    public final Setting<Integer> delay = this.register(new Setting<>("Delay", 5, 0, 100));

    public final Setting<Mode> mode = this.register(new Setting<>("Mode", Mode.FILLED));
    public final Setting<ColorUtils.Color> color = this.register(new Setting<>("Color", new ColorUtils.Color(255, 0, 0, 80)));
    public final Setting<Float> height = this.register(new Setting<>("Height", 1.0f, -1.0f, 2.0f, (v) -> mode.getValue() != Mode.FLAT && mode.getValue() != Mode.FLAT_OUTLINE));
    public final Setting<Float> width = this.register(new Setting<>("Width", 1.0f, 0.1f, 5.0f, (v) -> mode.getValue() == Mode.FLAT_OUTLINE || mode.getValue() == Mode.FILLED_OUTLINE || mode.getValue() == Mode.OUTLINE));

    private final TickTimer timer = new TickTimer();
    private List<BlockPos> voidHoles = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        this.timer.tick();

        if (this.timer.passed(this.delay.getValue())) {
            this.timer.reset();
            this.voidHoles.clear();

            this.voidHoles = BlockUtil.getSphere(new BlockPos(mc.player.getPositionVector()), this.range.getValue().intValue(), this.range.getValue().intValue(), false, true, 0)
                    .stream()
                    .filter((pos) -> pos.y == 0 && BlockUtil.getBlockFromPos(pos) == Blocks.AIR)
                    .collect(Collectors.toList());
        }
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck() && !this.voidHoles.isEmpty()) {
            for (int i = 0; i < this.voidHoles.size(); ++i) {
                BlockPos pos = this.voidHoles.get(i);

                ColorUtils.Color c = this.color.getValue();
                int colour = ColorUtils.toRGBA(c.red, c.green, c.blue, c.alpha);

                AxisAlignedBB box = new AxisAlignedBB(pos);

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
