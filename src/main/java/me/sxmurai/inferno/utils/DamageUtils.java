package me.sxmurai.inferno.utils;

import me.sxmurai.inferno.features.Feature;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtils extends Feature {
    public static float calculateDamage(double x, double y, double z, float power, boolean causesFire, EntityPlayer target) {
        float doublePower = power * 2.0f;
        double distancedSize = target.getDistanceSq(x, y, z) / doublePower;
        double blast = target.world.getBlockDensity(new Vec3d(x, y, z), target.getEntityBoundingBox());

        double impact = (1.0 - distancedSize) * blast;
        float damage = (float) ((impact * impact + impact) / 2.0f * 7.0f * doublePower + 1.0);

        return getBlastReduction(target, getDamageMultiplier(damage), new Explosion(target.world, target, x, y, z, power, causesFire, true));
    }

    public static float getBlastReduction(EntityPlayer target, float damage, Explosion explosion) {
        DamageSource src = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, target.getTotalArmorValue(), (float) target.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        int enchantModifier = 0;
        try {
            enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(target.getArmorInventoryList(), src);
        } catch (NullPointerException ignored) { }

        float eof = MathHelper.clamp(enchantModifier, 0.0f, 20.0f);
        damage *= 1.0f - eof / 25.0f;

        if (target.isPotionActive(MobEffects.RESISTANCE)) {
            damage -= damage / 4.0f;
        }

        return Math.max(0.0f, damage);
    }

    public static float getDamageMultiplier(float damage) {
        int difficulty = mc.world.getDifficulty().getId();
        return damage * (difficulty == 0 ? 0 : (difficulty == 2 ? 1 : (difficulty == 1 ? 0.5f : 1.5f)));
    }
}
