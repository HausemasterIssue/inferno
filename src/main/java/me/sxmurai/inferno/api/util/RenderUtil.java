package me.sxmurai.inferno.api.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderUtil implements Util {
    // below was taken from cosmos, i'll end up replacing it soon but this seemed like the simplest solution to my non-math brain
    // https://github.com/linustouchtips/cosmos/blob/main/src/main/java/cope/cosmos/util/render/RenderUtil.java#L412#L457
    public static void drawRoundedRectangle(double x, double y, double width, double height, double radius, int hex) {
        ColorUtil.Color color = ColorUtil.getColor(hex);

        GL11.glPushAttrib(GL11.GL_POINTS);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2.0;
        y *= 2.0;

        width = (width * 2.0) + x;
        height = (height * 2.0) + y;

        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);

            double pi = Math.PI;

            int i;
            for (i = 0; i <= 90; ++i) {
                GL11.glVertex2d(x + radius + Math.sin(i * pi / 180.0) * radius * -1.0, y + radius + Math.cos(i * pi / 180.0) * radius * -1.0);
            }

            for (i = 90; i <= 180; ++i) {
                GL11.glVertex2d(x + radius + Math.sin(i * pi / 180.0) * radius * -1.0, height - radius + Math.cos(i * pi / 180.0) * radius * -1.0);
            }

            for (i = 0; i <= 90; ++i) {
                GL11.glVertex2d(width - radius + Math.sin(i * pi / 180.0) * radius, height - radius + Math.cos(i * pi / 180.0) * radius);
            }

            for (i = 90; i <= 180; ++i) {
                GL11.glVertex2d(width - radius + Math.sin(i * pi / 180.0) * radius, y + radius + Math.cos(i * pi / 180.0) * radius);
            }

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();

        GL11.glScaled(2.0, 2.0, 0.0);
        GL11.glPopAttrib();
    }

    public static void drawHalfRoundedRectangle(double x, double y, double width, double height, double radius, int hex) {
        ColorUtil.Color color = ColorUtil.getColor(hex);

        GL11.glPushAttrib(GL11.GL_POINTS);
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2.0;
        y *= 2.0;

        width = (width * 2.0) + x;
        height = (height * 2.0) + y;

        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);

        double pi = Math.PI;

        int i;
        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d(x + radius + Math.sin(i * pi / 180.0) * radius * -1.0, y + radius + Math.cos(i * pi / 180.0) * radius * -1.0);
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d((x + 1.0) + Math.sin(i * pi / 180.0) * -1.0, (height - 1.0) + Math.cos(i * pi / 180.0) * -1.0);
        }

        for (i = 0; i <= 90; ++i) {
            GL11.glVertex2d((width - 1.0) + Math.sin(i * pi / 180.0), (height - 1.0) + Math.cos(i * pi / 180.0));
        }

        for (i = 90; i <= 180; ++i) {
            GL11.glVertex2d(width - radius + Math.sin(i * pi / 180.0) * radius, y + radius + Math.cos(i * pi / 180.0) * radius);
        }

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();

        GL11.glScaled(2.0, 2.0, 0.0);
        GL11.glPopAttrib();
    }

    public static void drawRectangle(double x, double y, double width, double height, int hex) {
        ColorUtil.Color color = ColorUtil.getColor(hex);

        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);

            GL11.glVertex2d(x, y + height);
            GL11.glVertex2d(x + width, y + height);
            GL11.glVertex2d(x + width, y);
            GL11.glVertex2d(x, y);

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
    }

    public static void drawCircle(double x, double y, double radius, int hex) {
        ColorUtil.Color color = ColorUtil.getColor(hex);

        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

            for (double angle = 0.0; angle < 2.0 * Math.PI; angle += 0.1) {
                GL11.glVertex2d(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius);
            }

        GL11.glEnd();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
    }

    public static void drawTexture(ResourceLocation location, int x, int y, int width, int height) {
        GL11.glPushMatrix();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);

        mc.getTextureManager().bindTexture(location);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        GL11.glPopMatrix();
    }

    public static void drawLine(double startX, double startY, double endX, double endY, float width, int hex) {
        ColorUtil.Color color = ColorUtil.getColor(hex);

        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glLineWidth(width);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        GL11.glBegin(GL11.GL_LINES);

            GL11.glVertex2d(startX, startY);
            GL11.glVertex2d(endX, endY);

        GL11.glEnd();
        GL11.glLineWidth(1.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    public static void scissor(int x, int y, int width, int height) {
        ScaledResolution resolution = new ScaledResolution(mc);
        GL11.glScissor(
                x * resolution.getScaleFactor(),
                (resolution.getScaledHeight() - height) * resolution.getScaleFactor(),
                (width - x) * resolution.getScaleFactor(),
                (height - y) * resolution.getScaleFactor()
        );
    }

    public static void drawFilledBox(AxisAlignedBB box, int hex) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_DEPTH);
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        ColorUtil.Color color = ColorUtil.getColor(hex);
        RenderGlobal.renderFilledBox(box, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawOutlinedBox(AxisAlignedBB box, float width, int hex) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glDisable(GL11.GL_DEPTH);
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glLineWidth(width);

        ColorUtil.Color color = ColorUtil.getColor(hex);
        RenderGlobal.drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        GL11.glLineWidth(1.0f);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawEsp(AxisAlignedBB box, boolean filled, boolean outline, float width, int hex) {
        if (filled) {
            drawFilledBox(box, hex);
        }

        if (outline) {
            drawOutlinedBox(box, width, hex);
        }
    }

    public static double interpolate(double start, double end) {
        return end + (start - end) * mc.getRenderPartialTicks();
    }

    public static Vec3d getScreen() {
        return new Vec3d(-mc.renderManager.renderPosX, -mc.renderManager.renderPosY, -mc.renderManager.renderPosZ);
    }

    public static AxisAlignedBB toScreen(AxisAlignedBB box) {
        return box.offset(getScreen());
    }
}
