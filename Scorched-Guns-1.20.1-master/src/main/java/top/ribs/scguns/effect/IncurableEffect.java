package top.ribs.scguns.effect;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class IncurableEffect extends MobEffect
{
    public IncurableEffect(MobEffectCategory typeIn, int liquidColorIn)
    {
        super(typeIn, liquidColorIn);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance)
    {
        cures.clear();
    }


}
