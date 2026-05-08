package top.ribs.scguns.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.ribs.scguns.compat.net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import top.ribs.scguns.Reference;
import top.ribs.scguns.init.ModEnchantments;
import top.ribs.scguns.init.ModTags;

import java.util.Random;

@EventBusSubscriber(modid = Reference.MOD_ID)
public final class CorrodedEnchantment {
    private static final Random RANDOM = new Random();

    private CorrodedEnchantment() {
    }

    private static void spawnCorrodedParticles(LivingEntity entity, int level) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < level * 5; i++) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * entity.getBbWidth();
                double offsetY = RANDOM.nextDouble() * entity.getBbHeight();
                double offsetZ = (RANDOM.nextDouble() - 0.5) * entity.getBbWidth();
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ, 1, 0, 0, 0, 0.1);
            }
        }
    }

    private static boolean isBotEntity(LivingEntity entity) {
        return entity.getType().is(ModTags.Entities.BOT);
    }

    public static float getBotDamageBonus(int level) {
        return level * 2.0F;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandItem();
            int corrodedLevel = weapon.getEnchantmentLevel(ModEnchantments.CORRODED);
            if (corrodedLevel > 0 && event.getEntity() instanceof LivingEntity target) {
                if (isBotEntity(target)) {
                    event.setAmount(event.getAmount() + getBotDamageBonus(corrodedLevel));
                    spawnCorrodedParticles(target, corrodedLevel);
                } else if (RANDOM.nextFloat() < 0.30F) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 60 + corrodedLevel * 20, 0));
                }
            }
        }
    }
}
