package me.sxmurai.inferno.features.modules.miscellaneous;

import java.util.HashMap;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.sxmurai.inferno.Inferno;
import me.sxmurai.inferno.features.settings.Setting;
import me.sxmurai.inferno.managers.modules.Module;
import me.sxmurai.inferno.managers.commands.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;


@Module.Define(name = "PearlNotify", description = "Tells you which direction a player threw a pearl")
public class PearlNotify extends Module {
	
	private final HashMap<EntityPlayer, UUID> list;
    private Entity enderPearl;
    private boolean flag;
	
    @Override
    public void onEnable() {
        this.flag = true;
    }

    @Override
    public void onUpdate() {
        if (PearlNotify.mc.world == null || PearlNotify.mc.player == null) {
            return;
        }
        this.enderPearl = null;
        for (final Entity e : PearlNotify.mc.world.loadedEntityList) {
            if (e instanceof EntityEnderPearl) {
                this.enderPearl = e;
                break;
            }
        }
        if (this.enderPearl == null) {
            this.flag = true;
            return;
        }
        EntityPlayer closestPlayer = null;
        for (final EntityPlayer entity : PearlNotify.mc.world.playerEntities) {
            if (closestPlayer == null) {
                closestPlayer = entity;
            } else {
                if (closestPlayer.getDistance(this.enderPearl) <= entity.getDistance(this.enderPearl)) {
                    continue;
                }
                closestPlayer = entity;
            }
        }
        if (closestPlayer == PearlNotify.mc.player) {
            this.flag = false;
        }
        if (closestPlayer != null && this.flag) {
            String faceing = this.enderPearl.getHorizontalFacing().toString();
            if (faceing.equals("WEST")) {
                faceing = "EAST";
            } else if (faceing.equals("EAST")) {
                faceing = "WEST";
            }
            Command.sendMessage((ChatFormatting.RED + closestPlayer.getName() + ChatFormatting.DARK_GRAY + " has just thrown a pearl heading " + ChatFormatting.RED + faceing + ChatFormatting.DARK_GRAY + "!"));
            this.flag = false;
        }
    }

}
