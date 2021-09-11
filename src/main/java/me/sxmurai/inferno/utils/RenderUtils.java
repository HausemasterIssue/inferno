package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.Feature;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class RenderUtils extends Feature {
    public static void drawTracer(Entity entity, float heightOffset, float width, float r, float g, float b, float a, boolean smooth, boolean steam) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.depthMask(false);
        GL11.glLineWidth(width);

        if (smooth) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }

        GlStateManager.disableDepth();

        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());

        Vec3d eyes = mc.player.getLookVec();
        Vec3d interpolated = toScreen(interpolateEntity(entity));

        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z);
        GL11.glVertex3d(interpolated.x, interpolated.y + heightOffset, interpolated.z);

        if (steam) {
            GL11.glVertex3d(interpolated.x, interpolated.y, interpolated.z);
            GL11.glVertex3d(interpolated.x, interpolated.y + entity.height, interpolated.z);
        }

        GL11.glEnd();

        if (smooth) {
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawRect(float x, float y, float width, float height, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        float alpha = (color >> 24 & 0xff) / 255f;
        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x, y + height, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + width, y + height, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x + width, y, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static void drawLine(double x, double y, double z, double x2, double y2, double z2, float width, boolean smooth, float r, float g, float b, float a) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.depthMask(false);
        GL11.glLineWidth(width);

        if (smooth) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        }

        GlStateManager.disableDepth();

        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();

        if (smooth) {
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawLine(double x, double y, double x2, double y2, float width, int color) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(width);

        float alpha = (color >> 24 & 0xff) / 255f;
        float red = (color >> 16 & 0xff) / 255f;
        float green = (color >> 8 & 0xff) / 255f;
        float blue = (color & 0xff) / 255f;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        buffer.pos(x2, y2, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();

        GlStateManager.glLineWidth(1.0f);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static double interpolate(double start, double end) {
        return end + (start - end) * mc.getRenderPartialTicks();
    }

    public static Vec3d interpolateVec(Vec3d start, Vec3d end) {
        return new Vec3d(interpolate(start.x, end.x), interpolate(start.y, end.y), interpolate(start.z, end.z));
    }

    public static Vec3d interpolateEntity(Entity entity) {
        return interpolateVec(new Vec3d(entity.posX, entity.posY, entity.posZ), new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ));
    }

    public static Vec3d toScreen(Vec3d interpolated) {
        return interpolated.subtract(mc.renderManager.renderPosX, mc.renderManager.renderPosY, mc.renderManager.renderPosZ);
    }

    public static float centerVertically(float y, float height) {
        return (y + (height / 2.0f)) - (Inferno.textManager.getHeight() / 2.0f);
    }

    public static float centerHorizontally(float x, float width, float textWidth) {
        return x + (width / 2.0f) - (textWidth / 2.0f);
    }
}