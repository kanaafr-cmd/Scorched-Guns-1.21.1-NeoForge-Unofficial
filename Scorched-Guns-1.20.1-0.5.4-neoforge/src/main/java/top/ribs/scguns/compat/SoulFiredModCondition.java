package top.ribs.scguns.compat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.fml.ModList;

public class SoulFiredModCondition implements ICondition {
    public static final SoulFiredModCondition INSTANCE = new SoulFiredModCondition();
    public static final MapCodec<SoulFiredModCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ICondition.IContext iContext) {
        return ModList.get().isLoaded("soul_fire_d");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
