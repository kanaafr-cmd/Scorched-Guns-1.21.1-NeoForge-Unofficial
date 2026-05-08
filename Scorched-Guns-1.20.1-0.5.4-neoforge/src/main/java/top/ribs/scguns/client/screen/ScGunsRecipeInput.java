package top.ribs.scguns.client.screen;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.ArrayList;
import java.util.List;

public record ScGunsRecipeInput(List<ItemStack> stacks) implements RecipeInput {
    public static ScGunsRecipeInput of(Container container) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            stacks.add(container.getItem(i).copy());
        }
        return new ScGunsRecipeInput(stacks);
    }

    public static ScGunsRecipeInput of(ItemStack stack) {
        return new ScGunsRecipeInput(List.of(stack));
    }

    @Override
    public ItemStack getItem(int index) {
        return index >= 0 && index < this.stacks.size() ? this.stacks.get(index) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return this.stacks.size();
    }
}
