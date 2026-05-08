package top.ribs.scguns.compat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.fml.ModList;

public class FarmersDelightModCondition implements ICondition {
    public static final FarmersDelightModCondition INSTANCE = new FarmersDelightModCondition();
    public static final MapCodec<FarmersDelightModCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ICondition.IContext iContext) {
        return ModList.get().isLoaded("farmersdelight");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
