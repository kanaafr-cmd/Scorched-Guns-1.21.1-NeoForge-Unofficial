package top.ribs.scguns.client.screen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import top.ribs.scguns.Reference;

public class GunBenchRecipe implements Recipe<ScGunsRecipeInput> {
    private ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final Ingredient blueprint;

    public GunBenchRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, Ingredient blueprint) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.blueprint = blueprint;
    }

    @Override
    public boolean matches(ScGunsRecipeInput input, Level level) {
        if (!blueprint.test(input.getItem(GunBenchMenu.SLOT_BLUEPRINT))) {
            return false;
        }
        for (int i = 0; i < recipeItems.size(); i++) {
            ItemStack stackInSlot = input.getItem(i);
            Ingredient requiredIngredient = recipeItems.get(i);
            if (!requiredIngredient.isEmpty() && !requiredIngredient.test(stackInSlot)) {
                return false;
            } else if (requiredIngredient.isEmpty() && !stackInSlot.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean matches(Container container, Level level) {
        return this.matches(ScGunsRecipeInput.of(container), level);
    }

    @Override
    public ItemStack assemble(ScGunsRecipeInput input, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(@NotNull HolderLookup.Provider registries) {
        return output.copy();
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    public Ingredient getBlueprint() {
        return blueprint;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public static class Type implements RecipeType<GunBenchRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "gun_bench";
    }

    public static class Serializer implements RecipeSerializer<GunBenchRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "gun_bench");

        private static final MapCodec<GunBenchRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Ingredient.CODEC.optionalFieldOf("gun_top_internal_1", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(0)),
                Ingredient.CODEC.optionalFieldOf("gun_top_internal_2", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(1)),
                Ingredient.CODEC.optionalFieldOf("gun_top_barrel_1", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(2)),
                Ingredient.CODEC.optionalFieldOf("gun_top_barrel_2", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(3)),
                Ingredient.CODEC.optionalFieldOf("gun_internal_1", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(4)),
                Ingredient.CODEC.optionalFieldOf("gun_internal_2", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(5)),
                Ingredient.CODEC.optionalFieldOf("gun_barrel_1", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(6)),
                Ingredient.CODEC.optionalFieldOf("gun_barrel_2", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(7)),
                Ingredient.CODEC.optionalFieldOf("gun_grip", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(8)),
                Ingredient.CODEC.optionalFieldOf("gun_magazine", Ingredient.EMPTY).forGetter(recipe -> recipe.recipeItems.get(9)),
                Ingredient.CODEC.optionalFieldOf("blueprint", Ingredient.EMPTY).forGetter(recipe -> recipe.blueprint)
        ).apply(instance, (output, topInternal1, topInternal2, topBarrel1, topBarrel2, internal1, internal2, barrel1, barrel2, grip, magazine, blueprint) -> {
            NonNullList<Ingredient> inputs = NonNullList.withSize(10, Ingredient.EMPTY);
            inputs.set(0, topInternal1);
            inputs.set(1, topInternal2);
            inputs.set(2, topBarrel1);
            inputs.set(3, topBarrel2);
            inputs.set(4, internal1);
            inputs.set(5, internal2);
            inputs.set(6, barrel1);
            inputs.set(7, barrel2);
            inputs.set(8, grip);
            inputs.set(9, magazine);
            return new GunBenchRecipe(ID, output, inputs, blueprint);
        }));

        private static final StreamCodec<RegistryFriendlyByteBuf, GunBenchRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    for (Ingredient ing : recipe.getIngredients()) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ing);
                    }
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.blueprint);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                },
                buffer -> {
                    NonNullList<Ingredient> inputs = NonNullList.withSize(10, Ingredient.EMPTY);
                    for (int i = 0; i < inputs.size(); i++) {
                        inputs.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    }
                    Ingredient blueprint = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
                    ItemStack output = RecipeCodecHelper.readItem(buffer);
                    return new GunBenchRecipe(ID, output, inputs, blueprint);
                }
        );

        @Override
        public MapCodec<GunBenchRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GunBenchRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
