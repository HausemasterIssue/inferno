package me.sxmurai.inferno.features.modules.render;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.events.mc.UpdateEvent;
import me.sxmurai.inferno.events.render.RenderEvent;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.HoleManager;
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

@Module.Define(name = "HoleESP", description = "Shows where safe holes are", category = Module.Category.RENDER)
public class HoleESP extends Module {
    public final Setting<Mode> mode = new Setting<>("Mode", Mode.FILLED);
    public final Setting<Boolean> stoodIn = new Setting<>("StoodIn", true);
    public final Setting<Float> range = new Setting<>("Range", 5.0f, 1.0f, 50.0f);

    public final Setting<Boolean> safe = new Setting<>("Safe", true);
    public final Setting<ColorUtils.Color> safeColor = new Setting<>("SafeColor", new ColorUtils.Color(0, 255, 0, 80), (v) -> safe.getValue());
    public final Setting<Boolean> unsafe = new Setting<>("Unsafe", true);
    public final Setting<ColorUtils.Color> unsafeColor = new Setting<>("UnsafeColor", new ColorUtils.Color(255, 0, 0, 80), (v) -> unsafe.getValue());

    public final Setting<Boolean> voidHoles = new Setting<>("Void", true);
    public final Setting<ColorUtils.Color> voidColor = new Setting<>("VoidColor", new ColorUtils.Color(255, 0, 0, 80), (v) -> voidHoles.getValue());

    public final Setting<Float> height = new Setting<>("Height", 1.0f, -1.0f, 2.0f, (v) -> mode.getValue() != Mode.FLAT && mode.getValue() != Mode.FLAT_OUTLINE);
    public final Setting<Float> width = new Setting<>("Width", 1.0f, 0.1f, 5.0f, (v) -> mode.getValue() == Mode.FLAT_OUTLINE || mode.getValue() == Mode.FILLED_OUTLINE || mode.getValue() == Mode.OUTLINE);

    private final TickTimer timer = new TickTimer();
    private List<BlockPos> voids = new ArrayList<>();

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (this.voidHoles.getValue()) {
            this.timer.tick();

            if (this.timer.passed(5)) {
                this.timer.reset();
                this.voids.clear();

                this.voids = BlockUtil.getSphere(new BlockPos(mc.player.getPositionVector()), this.range.getValue().intValue(), this.range.getValue().intValue(), false, true, 0)
                        .stream()
                        .filter((pos) -> pos.y == 0 && BlockUtil.getBlockFromPos(pos) == Blocks.AIR)
                        .collect(Collectors.toList());
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (!Module.fullNullCheck()) {
            if (!Inferno.holeManager.getHoles().isEmpty()) {
                for (HoleManager.Hole hole : Inferno.holeManager.getHoles()) {
                    if ((hole.isSafe() && !this.safe.getValue()) || (!hole.isSafe() && !this.unsafe.getValue())) {
                        continue;
                    }

                    BlockPos pos = hole.getPos();

                    if (BlockUtil.intersectsWith(pos) && !this.stoodIn.getValue()) {
                        continue;
                    }

                    if (mc.player.getDistance(pos.x, pos.y, pos.z) > this.range.getValue()) {
                        continue;
                    }

                    ColorUtils.Color c = hole.isSafe() ? this.safeColor.getValue() : this.unsafeColor.getValue();
                    this.renderBox(new AxisAlignedBB(pos), ColorUtils.toRGBA(c.red, c.green, c.blue, c.alpha));
                }
            }

            if (this.voidHoles.getValue() && !this.voids.isEmpty()) {
                for (int i = 0; i < this.voids.size(); ++i) {
                    BlockPos pos = this.voids.get(i);
                    ColorUtils.Color c = this.voidColor.getValue();
                    this.renderBox(new AxisAlignedBB(pos), ColorUtils.toRGBA(c.red, c.green, c.blue, c.alpha));
                }
            }
        }
    }

    private void renderBox(AxisAlignedBB box, int colour) {
        switch (this.mode.getValue()) {
            case FILLED: {
                RenderUtils.drawFilledBox(box.setMaxY(box.maxY - this.height.getValue()).offset(RenderUtils.screen()), colour);
                break;
            }

            case OUTLINE: {
                RenderUtils.drawOutlinedBox(box.setMaxY(box.maxY - this.height.getValue()).offset(RenderUtils.screen()), this.width.getValue(), colour);
                break;
            }

            case FILLED_OUTLINE: {
                RenderUtils.drawFilledBox(box.setMaxY(box.maxY - this.height.getValue()).offset(RenderUtils.screen()), colour);
                RenderUtils.drawOutlinedBox(box.setMaxY(box.maxY - this.height.getValue()).offset(RenderUtils.screen()), this.width.getValue(), colour);
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

    public enum Mode {
        FILLED, OUTLINE, FILLED_OUTLINE, FLAT, FLAT_OUTLINE
    }
}
