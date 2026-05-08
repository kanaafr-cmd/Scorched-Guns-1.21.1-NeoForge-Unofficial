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

public class PoweredMaceratorRecipe implements Recipe<ScGunsRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final int processingTime;
    private final int energyUse;
    private final ResourceLocation id;

    public PoweredMaceratorRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems, ItemStack output, int processingTime, int energyUse) {
        this.id = id;
        this.inputItems = inputItems;
        this.output = output;
        this.processingTime = processingTime;
        this.energyUse = energyUse;
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

    public int getEnergyUse() {
        return energyUse;
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

    public static class Type implements RecipeType<PoweredMaceratorRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "powered_macerating";
    }

    public static class Serializer implements RecipeSerializer<PoweredMaceratorRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "powered_macerating");

        private static final MapCodec<PoweredMaceratorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.optionalFieldOf("processingTime", 200).forGetter(PoweredMaceratorRecipe::getProcessingTime),
                Codec.INT.optionalFieldOf("energyUse", 1000).forGetter(PoweredMaceratorRecipe::getEnergyUse)
        ).apply(instance, (ingredients, output, processingTime, energyUse) ->
                new PoweredMaceratorRecipe(ID, RecipeCodecHelper.ingredients(ingredients), output, processingTime, energyUse)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PoweredMaceratorRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    RecipeCodecHelper.writeIngredients(buffer, recipe.inputItems);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                    buffer.writeVarInt(recipe.processingTime);
                    buffer.writeVarInt(recipe.energyUse);
                },
                buffer -> new PoweredMaceratorRecipe(ID, RecipeCodecHelper.readIngredients(buffer), RecipeCodecHelper.readItem(buffer), buffer.readVarInt(), buffer.readVarInt())
        );

        @Override
        public MapCodec<PoweredMaceratorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PoweredMaceratorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
