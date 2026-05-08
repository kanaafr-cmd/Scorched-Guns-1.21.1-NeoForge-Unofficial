package top.ribs.scguns.init;

import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;

/**
 * Registry for custom paintings
 */
public class ModPaintings {

    public static final DeferredRegister<PaintingVariant> REGISTER =
            DeferredRegister.create(Registries.PAINTING_VARIANT, Reference.MOD_ID);
    public static final DeferredHolder<PaintingVariant, PaintingVariant> THE_COLLECTIVE = REGISTER.register("the_collective",
            () -> new PaintingVariant(64, 32, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "the_collective")));
}
