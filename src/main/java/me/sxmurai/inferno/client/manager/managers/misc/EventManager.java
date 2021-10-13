package me.sxmurai.inferno.client.manager.managers.misc;

import joptsimple.internal.Strings;
import me.sxmurai.inferno.api.events.entity.DeathEvent;
import me.sxmurai.inferno.api.events.entity.TotemPopEvent;
import me.sxmurai.inferno.api.events.mc.UpdateEvent;
import me.sxmurai.inferno.api.events.network.ConnectionEvent;
import me.sxmurai.inferno.api.events.network.PacketEvent;
import me.sxmurai.inferno.api.events.network.SelfConnectionEvent;
import me.sxmurai.inferno.api.events.render.RenderEvent;
import me.sxmurai.inferno.api.utils.Wrapper;
import me.sxmurai.inferno.client.Inferno;
import me.sxmurai.inferno.client.manager.Manager;
import me.sxmurai.inferno.client.manager.managers.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

public class EventManager extends Manager {
    @SubscribeEvent
    public void onClientConnection(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        MinecraftForge.EVENT_BUS.post(new SelfConnectionEvent(SelfConnectionEvent.Type.CONNECT));
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        MinecraftForge.EVENT_BUS.post(new SelfConnectionEvent(SelfConnectionEvent.Type.DISCONNECT));
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!Wrapper.fullNullCheck() && event.getEntityLiving() == mc.player) {
            MinecraftForge.EVENT_BUS.post(new UpdateEvent());

            for (Module module : Inferno.moduleManager.getModules()) {
                if (!module.isToggled()) {
                    continue;
                }

                module.onUpdate();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Wrapper.fullNullCheck()) {
            for (Module module : Inferno.moduleManager.getModules()) {
                if (!module.isToggled()) {
                    continue;
                }

                module.onTick();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Module.fullNullCheck()) {
            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
                if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                    MinecraftForge.EVENT_BUS.post(new TotemPopEvent((EntityPlayer) packet.getEntity(mc.world)));
                }
            } else if (event.getPacket() instanceof SPacketEntityMetadata) {
                SPacketEntityMetadata packet = (SPacketEntityMetadata) event.getPacket();
                Entity entity = mc.world.getEntityByID(packet.getEntityId());
                if (entity instanceof EntityPlayer && (((EntityPlayer) entity).getHealth() <= 0.0f || entity.isDead)) {
                    MinecraftForge.EVENT_BUS.post(new DeathEvent((EntityPlayer) entity));
                }
            } else if (event.getPacket() instanceof SPacketPlayerListItem) {
                SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
                if (packet.getAction() != SPacketPlayerListItem.Action.ADD_PLAYER && packet.getAction() != SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                    return;
                }

                if (!packet.getEntries().isEmpty()) {
                    for (SPacketPlayerListItem.AddPlayerData data : packet.getEntries()) {
                        if (Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() == null) {
                            continue;
                        }

                        MinecraftForge.EVENT_BUS.post(new ConnectionEvent(
                                packet.getAction() == SPacketPlayerListItem.Action.ADD_PLAYER ? ConnectionEvent.Type.CONNECT : ConnectionEvent.Type.DISCONNECT,
                                data.getProfile().getId(),
                                data.getProfile().getName(),
                                mc.world.getPlayerEntityByUUID(data.getProfile().getId())
                        ));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableDepth();

        for (Module module : Inferno.moduleManager.getModules()) {
            if (!module.isToggled()) {
                continue;
            }

            mc.profiler.startSection("rw_" + module.getName());
            module.onRender3D(event.getPartialTicks());
            mc.profiler.endStartSection("rw_" + module.getName());
        }

        MinecraftForge.EVENT_BUS.post(new RenderEvent(RenderEvent.Type.WORLD));

        GlStateManager.glLineWidth(1.0f);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
}
