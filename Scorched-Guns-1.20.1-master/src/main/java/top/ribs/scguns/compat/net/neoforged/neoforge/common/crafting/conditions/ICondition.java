package top.ribs.scguns.compat.net.neoforged.neoforge.common.crafting.conditions;

import net.minecraft.resources.ResourceLocation;

public interface ICondition {
    ResourceLocation getID();

    boolean test(IContext context);

    interface IContext {
    }
}
