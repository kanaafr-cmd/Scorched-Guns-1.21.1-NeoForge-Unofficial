package dev.codex.architecturyfix.mixin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.codex.architecturyfix.ArchitecturyDsurroundModListFix;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

@Mixin(targets = "dev.architectury.platform.forge.PlatformImpl", remap = false)
public abstract class ArchitecturyPlatformImplMixin {
    @Inject(method = "getMods", at = @At("HEAD"), cancellable = true, remap = false)
    private static void architecturyDsurroundModListFix$getMods(CallbackInfoReturnable<Collection<?>> cir) {
        if (!isDynamicSurroundingsLookup()) {
            return;
        }

        Collection<Object> safeMods = new ArrayList<>();
        Method getMod = findGetModMethod();
        if (getMod == null) {
            return;
        }

        for (IModInfo modInfo : ModList.get().getMods()) {
            String modId = modInfo.getModId();
            try {
                safeMods.add(getMod.invoke(null, modId));
            }
            catch (InvocationTargetException ex) {
                ArchitecturyDsurroundModListFix.LOGGER.warn(
                    "Skipping mod id '{}' during Dynamic Surroundings Architectury resource lookup because Architectury could not wrap it.",
                    modId,
                    ex.getCause());
            }
            catch (ReflectiveOperationException | LinkageError ex) {
                ArchitecturyDsurroundModListFix.LOGGER.warn(
                    "Skipping mod id '{}' during Dynamic Surroundings Architectury resource lookup due to reflection failure.",
                    modId,
                    ex);
            }
        }

        cir.setReturnValue(safeMods);
    }

    private static Method findGetModMethod() {
        try {
            Class<?> platformImpl = Class.forName("dev.architectury.platform.forge.PlatformImpl");
            Method getMod = platformImpl.getDeclaredMethod("getMod", String.class);
            getMod.setAccessible(true);
            return getMod;
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            ArchitecturyDsurroundModListFix.LOGGER.warn("Could not locate Architectury PlatformImpl.getMod(String).", ex);
            return null;
        }
    }

    private static boolean isDynamicSurroundingsLookup() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if ("org.orecruncher.dsurround.lib.resources.ResourceLookupHelper".equals(element.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
