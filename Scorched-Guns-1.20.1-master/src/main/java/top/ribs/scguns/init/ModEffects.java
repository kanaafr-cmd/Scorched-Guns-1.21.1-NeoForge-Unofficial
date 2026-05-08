package top.ribs.scguns.init;

import top.ribs.scguns.Reference;
import top.ribs.scguns.effect.IncurableEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.effect.SulfurPoisoningEffect;

/**
 * Author: MrCrayfish
 */
public class    ModEffects
{
    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Reference.MOD_ID);

    public static final DeferredHolder<MobEffect, IncurableEffect> BLINDED = REGISTER.register("blinded", () -> new IncurableEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, IncurableEffect> DEAFENED = REGISTER.register("deafened", () -> new IncurableEffect(MobEffectCategory.HARMFUL, 0));
    public static final DeferredHolder<MobEffect, SulfurPoisoningEffect> SULFUR_POISONING = REGISTER.register("sulfur_poisoning",
            () -> new SulfurPoisoningEffect(MobEffectCategory.HARMFUL, 0xFFE135));

    @SuppressWarnings("unchecked")
    public static Holder<MobEffect> holder(DeferredHolder<MobEffect, ? extends MobEffect> effect) {
        return (Holder<MobEffect>) (Holder<?>) effect.getDelegate();
    }
}
