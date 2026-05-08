package top.ribs.scguns.init;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;
import top.ribs.scguns.world.NiterPatchConfiguration;
import top.ribs.scguns.world.NiterPatchFeature;
import top.ribs.scguns.world.VentFeature;
import top.ribs.scguns.world.VentFeatureConfiguration;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(BuiltInRegistries.FEATURE, Reference.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<VentFeatureConfiguration>> VENT_FEATURE = FEATURES.register("vent",
            () -> new VentFeature(VentFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, Feature<NiterPatchConfiguration>> NITER_PATCH = FEATURES.register("niter_patch",
            () -> new NiterPatchFeature(NiterPatchConfiguration.CODEC));


    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
