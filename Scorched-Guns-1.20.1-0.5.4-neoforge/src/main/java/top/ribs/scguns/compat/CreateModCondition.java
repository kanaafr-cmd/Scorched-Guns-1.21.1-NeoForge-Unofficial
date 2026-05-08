package top.ribs.scguns.compat;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.fml.ModList;

public class CreateModCondition implements ICondition {
    public static final CreateModCondition INSTANCE = new CreateModCondition();
    public static final MapCodec<CreateModCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(ICondition.IContext iContext) {
        return ModList.get().isLoaded("create");
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
