package top.ribs.scguns.client.screen;

import com.mojang.serialization.Codec;
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

import java.util.Iterator;

public class MechanicalPressRecipe implements Recipe<ScGunsRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final Ingredient moldItem;
    private final ItemStack output;
    private final int processingTime;
    private final ResourceLocation id;

    public MechanicalPressRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems, Ingredient moldItem, ItemStack output, int processingTime) {
        this.id = id;
        this.inputItems = inputItems;
        this.moldItem = moldItem;
        this.output = output;
        this.processingTime = processingTime;
    }

    public boolean requiresMold() {
        return !moldItem.isEmpty();
    }

    @Override
    public boolean matches(ScGunsRecipeInput inv, Level world) {
        if (world.isClientSide()) {
            return false;
        }
        NonNullList<Ingredient> requiredIngredients = NonNullList.create();
        requiredIngredients.addAll(inputItems);
        boolean moldMatched = moldItem.isEmpty() || moldItem.test(inv.getItem(3));
        for (int i = 0; i < inv.size(); i++) {
            if (i == 3) {
                continue;
            }
            ItemStack stackInSlot = inv.getItem(i);
            if (!stackInSlot.isEmpty()) {
                boolean matched = false;
                Iterator<Ingredient> iterator = requiredIngredients.iterator();
                while (iterator.hasNext()) {
                    Ingredient ingredient = iterator.next();
                    if (ingredient.test(stackInSlot)) {
                        iterator.remove();
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    continue;
                }
            }
        }
        return requiredIngredients.isEmpty() && moldMatched;
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

    public Ingredient getMoldItem() {
        return moldItem;
    }

    public static class Type implements RecipeType<MechanicalPressRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "mechanical_pressing";
    }

    public static class Serializer implements RecipeSerializer<MechanicalPressRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "mechanical_pressing");

        private static final MapCodec<MechanicalPressRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                Ingredient.CODEC.optionalFieldOf("mold", Ingredient.EMPTY).forGetter(MechanicalPressRecipe::getMoldItem),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.optionalFieldOf("processingTime", 200).forGetter(MechanicalPressRecipe::getProcessingTime)
        ).apply(instance, (ingredients, mold, output, processingTime) ->
                new MechanicalPressRecipe(ID, RecipeCodecHelper.ingredients(ingredients), mold, output, processingTime)));

        private static final StreamCodec<RegistryFriendlyByteBuf, MechanicalPressRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    RecipeCodecHelper.writeIngredients(buffer, recipe.inputItems);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.moldItem);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                    buffer.writeVarInt(recipe.processingTime);
                },
                buffer -> new MechanicalPressRecipe(ID, RecipeCodecHelper.readIngredients(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), RecipeCodecHelper.readItem(buffer), buffer.readVarInt())
        );

        @Override
        public MapCodec<MechanicalPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MechanicalPressRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
