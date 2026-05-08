package top.ribs.scguns.compat.net.neoforged.neoforge.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public final class ForgeHooks {
    private ForgeHooks() {
    }

    public static int getBurnTime(ItemStack stack, RecipeType<?> recipeType) {
        return stack.getBurnTime(recipeType);
    }
}
