package dev.codex.theworldpatches.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.sonicether.soundphysics.config.ReverbParams", remap = false)
public abstract class SoundPhysicsReverbParamsMixin {
    @Inject(method = "getReverb0()Lcom/sonicether/soundphysics/config/ReverbParams;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$getReverb0(CallbackInfoReturnable<Object> cir) {
        if (the_world_compat_patches$configMissing()) {
            cir.setReturnValue(the_world_compat_patches$reverb(0.15F, 0.0F, 1.0F, 0.2F * 0.7F * 0.85F, 0.99F, 0.6F, 2.5F, 0.001F, 1.26F, 0.011F, 0.994F, 0.16F));
        }
    }

    @Inject(method = "getReverb1()Lcom/sonicether/soundphysics/config/ReverbParams;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$getReverb1(CallbackInfoReturnable<Object> cir) {
        if (the_world_compat_patches$configMissing()) {
            cir.setReturnValue(the_world_compat_patches$reverb(0.55F, 0.0F, 1.0F, 0.3F * 0.7F * 0.85F, 0.99F, 0.7F, 0.2F, 0.015F, 1.26F, 0.011F, 0.994F, 0.15F));
        }
    }

    @Inject(method = "getReverb2()Lcom/sonicether/soundphysics/config/ReverbParams;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$getReverb2(CallbackInfoReturnable<Object> cir) {
        if (the_world_compat_patches$configMissing()) {
            cir.setReturnValue(the_world_compat_patches$reverb(1.68F, 0.1F, 1.0F, 0.5F * 0.7F * 0.85F, 0.99F, 0.7F, 0.0F, 0.021F, 1.26F, 0.021F, 0.994F, 0.13F));
        }
    }

    @Inject(method = "getReverb3()Lcom/sonicether/soundphysics/config/ReverbParams;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void the_world_compat_patches$getReverb3(CallbackInfoReturnable<Object> cir) {
        if (the_world_compat_patches$configMissing()) {
            cir.setReturnValue(the_world_compat_patches$reverb(4.142F, 0.5F, 1.0F, 0.4F * 0.7F * 0.85F, 0.89F, 0.7F, 0.0F, 0.025F, 1.26F, 0.021F, 0.994F, 0.11F));
        }
    }

    @Unique
    private static boolean the_world_compat_patches$configMissing() {
        try {
            Class<?> modClass = Class.forName("com.sonicether.soundphysics.SoundPhysicsMod");
            Field config = modClass.getField("CONFIG");
            return config.get(null) == null;
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            return false;
        }
    }

    @Unique
    private static Object the_world_compat_patches$reverb(float decayTime, float density, float diffusion, float gain, float gainHF, float decayHFRatio, float reflectionsGain, float reflectionsDelay, float lateReverbGain, float lateReverbDelay, float airAbsorptionGainHF, float roomRolloffFactor) {
        try {
            Class<?> paramsClass = Class.forName("com.sonicether.soundphysics.config.ReverbParams");
            Object params = paramsClass.getConstructor().newInstance();
            the_world_compat_patches$set(paramsClass, params, "decayTime", decayTime);
            the_world_compat_patches$set(paramsClass, params, "density", density);
            the_world_compat_patches$set(paramsClass, params, "diffusion", diffusion);
            the_world_compat_patches$set(paramsClass, params, "gain", gain);
            the_world_compat_patches$set(paramsClass, params, "gainHF", gainHF);
            the_world_compat_patches$set(paramsClass, params, "decayHFRatio", decayHFRatio);
            the_world_compat_patches$set(paramsClass, params, "reflectionsGain", reflectionsGain);
            the_world_compat_patches$set(paramsClass, params, "reflectionsDelay", reflectionsDelay);
            the_world_compat_patches$set(paramsClass, params, "lateReverbGain", lateReverbGain);
            the_world_compat_patches$set(paramsClass, params, "lateReverbDelay", lateReverbDelay);
            the_world_compat_patches$set(paramsClass, params, "airAbsorptionGainHF", airAbsorptionGainHF);
            the_world_compat_patches$set(paramsClass, params, "roomRolloffFactor", roomRolloffFactor);
            return params;
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            return null;
        }
    }

    @Unique
    private static void the_world_compat_patches$set(Class<?> paramsClass, Object params, String fieldName, float value) throws ReflectiveOperationException {
        Field field = paramsClass.getField(fieldName);
        field.setFloat(params, value);
    }
}
