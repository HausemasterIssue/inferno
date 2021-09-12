package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import org.lwjgl.input.Mouse;

public class EntityUtils extends Feature {
    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isPassive(Entity entity) {
        return entity instanceof EntityAmbientCreature || entity instanceof EntitySquid || entity instanceof EntityAnimal;
    }

    public static boolean isHostile(Entity entity) {
        return !isPassive(entity) || entity instanceof EntitySlime || entity instanceof EntityMob && ((EntityMob) entity).getRevengeTarget() != null;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLiving && !entity.isDead;
    }

    public static boolean isInvisible(EntityLiving entity) {
        return entity.isInvisibleToPlayer(mc.player) && entity.isPotionActive(MobEffects.INVISIBILITY);
    }

    public static boolean isProjectile(Entity entity) {
        return entity instanceof IProjectile;
    }

    public static float getHealth(Entity entity) {
        if (!isLiving(entity)) {
            return 0.0f;
        }

        return ((EntityLiving) entity).getHealth() + ((EntityLiving) entity).getAbsorptionAmount();
    }
    
    public static boolean isEating() {
        return mc.player.getHeldItemMainhand().getItemUseAction().equals(EnumAction.EAT) || mc.player.getHeldItemMainhand().getItemUseAction().equals(EnumAction.DRINK);
    }

    public static boolean isMending() {
        return InventoryUtils.isHolding(Items.EXPERIENCE_BOTTLE) && Mouse.isButtonDown(1);
    }

    public static boolean isMining() {
        return InventoryUtils.isHolding(Items.DIAMOND_PICKAXE) && mc.playerController.getIsHittingBlock();
    }
}
