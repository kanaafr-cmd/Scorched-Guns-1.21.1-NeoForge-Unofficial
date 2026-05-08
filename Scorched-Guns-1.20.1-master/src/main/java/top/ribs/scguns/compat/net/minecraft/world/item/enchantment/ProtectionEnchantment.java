package top.ribs.scguns.compat.net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.LivingEntity;

public final class ProtectionEnchantment {
    private ProtectionEnchantment() {
    }

    public static double getExplosionKnockbackAfterDampener(LivingEntity entity, double knockback) {
        return knockback;
    }
}
