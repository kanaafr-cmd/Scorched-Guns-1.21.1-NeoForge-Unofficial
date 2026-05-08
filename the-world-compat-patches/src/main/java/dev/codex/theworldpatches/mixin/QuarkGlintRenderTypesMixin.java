package dev.codex.theworldpatches.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resources.ResourceLocation;

@Mixin(targets = "org.violetmoon.quark.content.tools.client.render.GlintRenderTypes", remap = false)
public abstract class QuarkGlintRenderTypesMixin {
    @Inject(method = "texture(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$texture(String color, CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(ResourceLocation.fromNamespaceAndPath("quark", "textures/misc/glint_" + color + ".png"));
    }
}
