package dev.codex.theworldpatches.mixin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

@Mixin(targets = "top.ribs.scguns.init.ModItems", remap = false)
public abstract class ScorchedGunsModItemsMixin {
    @Inject(method = "lambda$registerItems$0()Lnet/minecraft/world/item/Item;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$createAnthraliteKnife(CallbackInfoReturnable<Item> cir) {
        try {
            Class<?> modTiers = Class.forName("top.ribs.scguns.init.ModTiers");
            Field anthralite = modTiers.getField("ANTHRALITE");
            Tier tier = (Tier) anthralite.get(null);

            Class<?> knifeClass = Class.forName("vectorwing.farmersdelight.common.item.KnifeItem");
            Constructor<?> modernCtor = knifeClass.getConstructor(Tier.class, Item.Properties.class);
            Object knife = modernCtor.newInstance(tier, new Item.Properties());
            cir.setReturnValue((Item) knife);
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            the_world_compat_patches$rethrow(ex);
        }
    }

    @Unique
    private static void the_world_compat_patches$rethrow(Throwable throwable) {
        if (throwable instanceof RuntimeException runtimeException) {
            throw runtimeException;
        }
        throw new RuntimeException("Failed to create ANTHRALITE_KNIFE with Farmer's Delight 1.21 constructor", throwable);
    }
}
