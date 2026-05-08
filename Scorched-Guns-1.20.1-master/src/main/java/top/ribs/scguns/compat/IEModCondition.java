package top.ribs.scguns.compat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.fml.ModList;

public class IEModCondition implements ICondition {
    public static final IEModCondition INSTANCE = new IEModCondition();
    public static final MapCodec<IEModCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ICondition.IContext iContext) {
        return ModList.get().isLoaded("immersiveengineering");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
