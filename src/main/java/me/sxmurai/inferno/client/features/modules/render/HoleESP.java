package me.sxmurai.inferno.client.features.modules.render;

import me.sxmurai.inferno.api.utils.BlockUtil;
import me.sxmurai.inferno.api.utils.ColorUtils;
import me.sxmurai.inferno.api.utils.RenderUtils;
import me.sxmurai.inferno.api.utils.timing.TickTimer;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.managers.misc.HoleManager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Module.Define(name = "HoleESP", description = "Shows where safe holes are", category = Module.Category.RENDER)
public class HoleESP extends Module {
    public final Value<Mode> mode = new Value<>("Mode", Mode.FILLED);
    public final Value<Boolean> stoodIn = new Value<>("StoodIn", true);
    public final Value<Float> range = new Value<>("Range", 5.0f, 1.0f, 50.0f);

    public final Value<Boolean> safe = new Value<>("Safe", true);
    public final Value<ColorUtils.Color> safeColor = new Value<>("SafeColor", new ColorUtils.Color(0, 255, 0, 80), (v) -> safe.getValue());
    public final Value<Boolean> unsafe = new Value<>("Unsafe", true);
    public final Value<ColorUtils.Color> unsafeColor = new Value<>("UnsafeColor", new ColorUtils.Color(255, 0, 0, 80), (v) -> unsafe.getValue());

    public final Value<Boolean> voidHoles = new Value<>("Void", true);
    public final Value<ColorUtils.Color> voidColor = new Value<>("VoidColor", new ColorUtils.Color(255, 0, 0, 80), (v) -> voidHoles.getValue());

    public final Value<Float> height = new Value<>("Height", 1.0f, -1.0f, 2.0f, (v) -> mode.getValue() != Mode.FLAT && mode.getValue() != Mode.FLAT_OUTLINE);
    public final Value<Float> width = new Value<>("Width", 1.0f, 0.1f, 5.0f, (v) -> mode.getValue() == Mode.FLAT_OUTLINE || mode.getValue() == Mode.FILLED_OUTLINE || mode.getValue() == Mode.OUTLINE);

    private final TickTimer timer = new TickTimer();
    private List<BlockPos> voids = new ArrayList<>();

    @Override
    public void onUpdate() {
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

    @Override
    public void onRender3D(float partialTicks) {
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
