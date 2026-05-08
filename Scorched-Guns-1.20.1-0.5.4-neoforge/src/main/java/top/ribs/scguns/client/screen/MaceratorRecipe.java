package top.ribs.scguns.client.screen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import top.ribs.scguns.Reference;

import java.util.List;

public class MaceratorRecipe implements Recipe<ScGunsRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final int processingTime;
    private final ResourceLocation id;

    public MaceratorRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems, ItemStack output, int processingTime) {
        this.id = id;
        this.inputItems = inputItems;
        this.output = output;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(ScGunsRecipeInput inv, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        NonNullList<Ingredient> requiredIngredients = NonNullList.create();
        requiredIngredients.addAll(inputItems);
        for (int i = 0; i < inv.size(); i++) {
            ItemStack stackInSlot = inv.getItem(i);
            if (!stackInSlot.isEmpty()) {
                boolean matched = false;
                for (Ingredient ingredient : List.copyOf(requiredIngredients)) {
                    if (ingredient.test(stackInSlot)) {
                        requiredIngredients.remove(ingredient);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
        }
        return requiredIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(ScGunsRecipeInput inv, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public static class Type implements RecipeType<MaceratorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "macerating";
    }

    public static class Serializer implements RecipeSerializer<MaceratorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "macerating");

        private static final MapCodec<MaceratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                com.mojang.serialization.Codec.INT.optionalFieldOf("processingTime", 200).forGetter(MaceratorRecipe::getProcessingTime)
        ).apply(instance, (ingredients, output, processingTime) ->
                new MaceratorRecipe(ID, RecipeCodecHelper.ingredients(ingredients), output, processingTime)));

        private static final StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    RecipeCodecHelper.writeIngredients(buffer, recipe.inputItems);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                    buffer.writeVarInt(recipe.processingTime);
                },
                buffer -> new MaceratorRecipe(ID, RecipeCodecHelper.readIngredients(buffer), RecipeCodecHelper.readItem(buffer), buffer.readVarInt())
        );

        @Override
        public MapCodec<MaceratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MaceratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
