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

import static top.ribs.scguns.blockentity.PoweredMechanicalPressBlockEntity.MOLD_SLOT;

public class PoweredMechanicalPressRecipe implements Recipe<ScGunsRecipeInput> {
    private final NonNullList<Ingredient> inputItems;
    private final Ingredient moldItem;
    private final ItemStack output;
    private final int processingTime;
    private final int energyUse;
    private final ResourceLocation id;

    public PoweredMechanicalPressRecipe(ResourceLocation id, NonNullList<Ingredient> inputItems, Ingredient moldItem, ItemStack output, int processingTime, int energyUse) {
        this.id = id;
        this.inputItems = inputItems;
        this.moldItem = moldItem;
        this.output = output;
        this.processingTime = processingTime;
        this.energyUse = energyUse;
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
        boolean moldMatched = moldItem.isEmpty() || moldItem.test(inv.getItem(MOLD_SLOT));
        for (int i = 0; i < inv.size(); i++) {
            if (i == MOLD_SLOT) {
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

    public Ingredient getMoldItem() {
        return moldItem;
    }

    public static class Type implements RecipeType<PoweredMechanicalPressRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "powered_mechanical_pressing";
    }

    public static class Serializer implements RecipeSerializer<PoweredMechanicalPressRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "powered_mechanical_pressing");

        private static final MapCodec<PoweredMechanicalPressRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> recipe.inputItems),
                Ingredient.CODEC.optionalFieldOf("mold", Ingredient.EMPTY).forGetter(PoweredMechanicalPressRecipe::getMoldItem),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.optionalFieldOf("processingTime", 200).forGetter(PoweredMechanicalPressRecipe::getProcessingTime),
                Codec.INT.optionalFieldOf("energyUse", 1000).forGetter(PoweredMechanicalPressRecipe::getEnergyUse)
        ).apply(instance, (ingredients, mold, output, processingTime, energyUse) ->
                new PoweredMechanicalPressRecipe(ID, RecipeCodecHelper.ingredients(ingredients), mold, output, processingTime, energyUse)));

        private static final StreamCodec<RegistryFriendlyByteBuf, PoweredMechanicalPressRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    RecipeCodecHelper.writeIngredients(buffer, recipe.inputItems);
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.moldItem);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                    buffer.writeVarInt(recipe.processingTime);
                    buffer.writeVarInt(recipe.energyUse);
                },
                buffer -> new PoweredMechanicalPressRecipe(ID, RecipeCodecHelper.readIngredients(buffer), Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), RecipeCodecHelper.readItem(buffer), buffer.readVarInt(), buffer.readVarInt())
        );

        @Override
        public MapCodec<PoweredMechanicalPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PoweredMechanicalPressRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
