package top.ribs.scguns.client.screen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
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

public class LightningBatteryRecipe implements Recipe<ScGunsRecipeInput> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;
    private final int processingTime;
    private final int energyUse;

    public LightningBatteryRecipe(ResourceLocation id, Ingredient input, ItemStack output, int processingTime, int energyUse) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.processingTime = processingTime;
        this.energyUse = energyUse;
    }

    @Override
    public boolean matches(ScGunsRecipeInput input, Level level) {
        return this.input.test(input.getItem(0));
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
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output.copy();
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public Ingredient getInput() {
        return input;
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
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    public static class Type implements RecipeType<LightningBatteryRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "lightning_battery";
    }

    public static class Serializer implements RecipeSerializer<LightningBatteryRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "lightning_battery");

        private static final MapCodec<LightningBatteryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(recipe -> java.util.List.of(recipe.input)),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.INT.optionalFieldOf("processingTime", 200).forGetter(LightningBatteryRecipe::getProcessingTime),
                Codec.INT.optionalFieldOf("requiredEnergy", 1000).forGetter(LightningBatteryRecipe::getEnergyUse)
        ).apply(instance, (inputs, output, processingTime, requiredEnergy) ->
                new LightningBatteryRecipe(ID, inputs.getFirst(), output, processingTime, requiredEnergy)));

        private static final StreamCodec<RegistryFriendlyByteBuf, LightningBatteryRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
                    RecipeCodecHelper.writeItem(buffer, recipe.output);
                    buffer.writeVarInt(recipe.processingTime);
                    buffer.writeVarInt(recipe.energyUse);
                },
                buffer -> new LightningBatteryRecipe(ID, Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), RecipeCodecHelper.readItem(buffer), buffer.readVarInt(), buffer.readVarInt())
        );

        @Override
        public MapCodec<LightningBatteryRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LightningBatteryRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
