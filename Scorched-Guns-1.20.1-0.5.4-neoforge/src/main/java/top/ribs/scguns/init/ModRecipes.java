package top.ribs.scguns.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;
import top.ribs.scguns.client.screen.*;


public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, Reference.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, Reference.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<MechanicalPressRecipe>> MECHANICAL_PRESS_TYPE =
            TYPES.register("mechanical_pressing", () -> MechanicalPressRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PoweredMechanicalPressRecipe>> POWERED_MECHANICAL_PRESS_TYPE =
            TYPES.register("powered_mechanical_pressing", () -> PoweredMechanicalPressRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MaceratorRecipe>> MACERATOR_TYPE =
            TYPES.register("macerating", () -> MaceratorRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<PoweredMaceratorRecipe>> POWERED_MACERATOR_TYPE =
            TYPES.register("powered_macerating", () -> PoweredMaceratorRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<GunBenchRecipe>> GUN_BENCH_TYPE =
            TYPES.register("gun_bench", () -> GunBenchRecipe.Type.INSTANCE);
    public static final DeferredHolder<RecipeType<?>, RecipeType<LightningBatteryRecipe>> LIGHTNING_BATTERY_TYPE =
            TYPES.register("lightning_battery", () -> LightningBatteryRecipe.Type.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MechanicalPressRecipe>> MECHANICAL_PRESS_SERIALIZER =
            SERIALIZERS.register("mechanical_pressing", () -> MechanicalPressRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PoweredMechanicalPressRecipe>> POWERED_MECHANICAL_PRESS_SERIALIZER =
            SERIALIZERS.register("powered_mechanical_pressing", () -> PoweredMechanicalPressRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MaceratorRecipe>> MACERATOR_SERIALIZER =
            SERIALIZERS.register("macerating", () -> MaceratorRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PoweredMaceratorRecipe>> POWERED_MACERATOR_SERIALIZER =
            SERIALIZERS.register("powered_macerating", () -> PoweredMaceratorRecipe.Serializer.INSTANCE);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GunBenchRecipe>> GUN_BENCH_SERIALIZER =
            SERIALIZERS.register("gun_bench", () -> GunBenchRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LightningBatteryRecipe>> LIGHTNING_BATTERY_SERIALIZER =
            SERIALIZERS.register("lightning_battery", () -> LightningBatteryRecipe.Serializer.INSTANCE);
    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}



