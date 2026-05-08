package top.ribs.scguns.client.screen;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

final class RecipeCodecHelper {
    private RecipeCodecHelper() {
    }

    static NonNullList<Ingredient> ingredients(List<Ingredient> ingredients) {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(ingredients);
        return list;
    }

    static void writeIngredients(RegistryFriendlyByteBuf buffer, NonNullList<Ingredient> ingredients) {
        buffer.writeVarInt(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }
    }

    static NonNullList<Ingredient> readIngredients(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
        for (int i = 0; i < size; i++) {
            ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        }
        return ingredients;
    }

    static void writeItem(RegistryFriendlyByteBuf buffer, ItemStack stack) {
        ItemStack.STREAM_CODEC.encode(buffer, stack);
    }

    static ItemStack readItem(RegistryFriendlyByteBuf buffer) {
        return ItemStack.STREAM_CODEC.decode(buffer);
    }
}
