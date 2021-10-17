package me.sxmurai.inferno.client.features.modules.movement;

import me.sxmurai.inferno.api.events.entity.MoveEvent;
import me.sxmurai.inferno.api.values.Value;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// NCP packets taken from https://github.com/IUDevman/gamesense-client/blob/master/src/main/java/com/gamesense/client/module/modules/movement/Step.java
// i somehow made the code more shit, but i'll end up improving later. atleast thats what i tell myself
@Module.Define(name = "Step", description = "Allows you to step up blocks", category = Module.Category.MOVEMENT)
public class Step extends Module {
    private static final double[][] COLLISIONS = new double[][] {
            new double[] { 2.6, 2.4 },
            new double[] { 2.1, 1.9 },
            new double[] { 1.6, 1.4 },
            new double[] { 1.0, 0.6 }
    };

    private static final double[] ONE_BLOCK_NCP = new double[] { 0.42, 0.753 };
    private static final double[] ONE_HALF_BLOCK_NCP = new double[] { 0.42, 0.75, 1.0, 1.16, 1.23, 1.2 };
    private static final double[] TWO_BLOCK_NCP = new double[] { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43 };
    private static final double[] TWO_HALF_BLOCK_NCP = new double[] { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };

    public final Value<Mode> mode = new Value<>("Mode", Mode.NCP);
    public final Value<Double> velocity = new Value<>("Velocity", 0.3, 0.1, 10.0, (v) -> mode.getValue() == Mode.SPIDER);
    public final Value<Double> height = new Value<>("Height", 1.0, 1.0, 2.5);

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (!mc.player.onGround) {
            return;
        }

        double height = 0.0;
        for (double[] offsets : Step.COLLISIONS) {
            if (!this.isBoundingBoxEmpty(event.getX(), offsets[0], event.getZ()) && this.isBoundingBoxEmpty(event.getX(), offsets[1], event.getZ())) {
                double h = offsets[0];
                if (h == 1.6) {
                    h = 1.5;
                } else if (h == 2.6) {
                    h = 2.5;
                }

                height = h;
            }
        }

        if (height != 0.0 && this.height.getValue() > height) {
            switch (this.mode.getValue()) {
                case NCP: {
                    double[] heights = this.getPacketHeightOffsets(height);
                    if (heights == null || heights.length == 0) {
                        return;
                    }

                    for (double offset : heights) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset, mc.player.posZ, mc.player.onGround));
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + height, mc.player.posZ);
                    break;
                }

                case VANILLA: {
                    mc.player.stepHeight = this.height.getValue().floatValue();
                    break;
                }

                case SPIDER: {
                    mc.player.motionY = this.velocity.getValue();
                    if (mc.player.ticksExisted % 2 == 0) {
                        mc.player.motionY -= 0.28;
                    }
                    break;
                }
            }
        }
    }

    private double[] getPacketHeightOffsets(double h) {
        if (h == 1.0) {
            return ONE_BLOCK_NCP;
        } else if (h == 1.5) {
            return ONE_HALF_BLOCK_NCP;
        } else if (h == 2.0) {
            return TWO_BLOCK_NCP;
        } else if (h == 2.5) {
            return TWO_HALF_BLOCK_NCP;
        }

        return null;
    }

    private boolean isBoundingBoxEmpty(double x, double y, double z) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, y, z)).isEmpty();
    }

    public enum Mode {
        NCP, VANILLA, SPIDER
    }
}
