package top.ribs.scguns.entity.ai;

import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import top.ribs.scguns.compat.net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.ribs.scguns.Reference;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

@EventBusSubscriber(modid = Reference.MOD_ID)
public class GunMobFriendlyFire {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Mob victim)) return;

        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();

        if (!(attacker instanceof Mob mobAttacker)) return;

        if (shouldPreventFriendlyFire(victim, mobAttacker)) {
            event.setAmount(0.0F);
            victim.setLastHurtByMob(null);
        }
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob)) return;

        LivingEntity newTarget = event.getNewAboutToBeSetTarget();
        if (!(newTarget instanceof Mob targetMob)) return;

        if (shouldPreventFriendlyFire(targetMob, mob)) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    private static boolean shouldPreventFriendlyFire(Mob victim, Mob attacker) {
        if (isInSameRaid(victim, attacker)) {
            return true;
        }

        if (!victim.getTags().contains("MobGunner") || !attacker.getTags().contains("MobGunner")) {
            return false;
        }

        AIType victimAI = getAIType(victim);
        AIType attackerAI = getAIType(attacker);

        if (victimAI == AIType.RECKLESS && attackerAI == AIType.RECKLESS) {
            return false;
        }

        return victimAI == AIType.TACTICAL || victimAI == AIType.DEFAULT ||
                attackerAI == AIType.TACTICAL || attackerAI == AIType.DEFAULT;
    }

    private static AIType getAIType(Mob mob) {
        if (mob.getTags().contains("AI_TACTICAL")) return AIType.TACTICAL;
        if (mob.getTags().contains("AI_DEFAULT")) return AIType.DEFAULT;
        if (mob.getTags().contains("AI_RECKLESS")) return AIType.RECKLESS;
        if (mob.getTags().contains("AI_COWARD")) return AIType.COWARD;
        return AIType.DEFAULT;
    }

    private static boolean isInSameRaid(Mob victim, Mob attacker) {
        for (String tag : victim.getTags()) {
            if (tag.startsWith("RaidMember_") && attacker.getTags().contains(tag)) {
                return true;
            }
        }
        return false;
    }
}
