package me.sxmurai.inferno.api.utils;

import me.sxmurai.inferno.client.Inferno;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class EntityUtils extends Wrapper {
    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isPassive(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.CREATURE, false) || entity.isCreatureType(EnumCreatureType.AMBIENT, false);
    }

    public static boolean isHostile(Entity entity) {
        return entity.isCreatureType(EnumCreatureType.MONSTER, false) && !isPassive(entity);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isInvisible(EntityLivingBase entity) {
        return entity.isInvisibleToPlayer(mc.player) && entity.isPotionActive(MobEffects.INVISIBILITY);
    }

    public static float getHealth(Entity entity) {
        if (!isLiving(entity)) {
            return 0.0f;
        }

        return ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount();
    }

    public static boolean isRiding(boolean controllerCheck) {
        return mc.player.isRiding() && (controllerCheck && mc.player.ridingEntity.getControllingPassenger() == mc.player);
    }

    public static EntityPlayer getClosest(EntityPlayer previous, float range, boolean friends) {
        EntityPlayer player = previous;
        for (EntityPlayer p : mc.world.playerEntities) {
            if (p == null || p == mc.player || mc.player.getDistance(p) > range || (!friends && Inferno.friendManager.isFriend(p))) {
                continue;
            }

            if (player == null) {
                player = p;
                continue;
            }

            if (mc.player.getDistance(player) > mc.player.getDistance(p)) {
                player = p;
            }
        }

        return player;
    }
}
