package dev.codex.creatingspacefix.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import dev.codex.creatingspacefix.CreatingSpaceConfigLoadFix;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mixin(ModConfigSpec.ConfigValue.class)
public abstract class ConfigValueEarlyOverlayMixin<T> {
    @Shadow
    private Supplier<T> defaultSupplier;

    @Inject(
        method = "getRaw()Ljava/lang/Object;",
        at = @At(
            value = "INVOKE",
            target = "Lcom/google/common/base/Preconditions;checkState(ZLjava/lang/Object;)V",
            shift = At.Shift.BEFORE),
        cancellable = true,
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void creatingSpaceConfigLoadFix$defaultForEarlyClientInit(CallbackInfoReturnable<T> cir, ILoadedConfig loadedConfig) {
        String earlyPath = findEarlyClientInitPath();
        if (loadedConfig == null && earlyPath != null) {
            T fallback = this.defaultSupplier.get();
            CreatingSpaceConfigLoadFix.LOGGER.warn(
                "Client init path {} requested a config value before configs loaded; using default {} for this early init/recovery read.",
                earlyPath,
                fallback);
            cir.setReturnValue(fallback);
        }
    }

    private static String findEarlyClientInitPath() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            if ("com.rae.creatingspace.content.life_support.spacesuit.RemainingO2Overlay".equals(className)) {
                return className;
            }
            if ("com.simibubi.create.infrastructure.gui.OpenCreateMenuButton$OpenConfigButtonHandler".equals(className)) {
                return className;
            }
            if ("net.minecraft.client.Minecraft".equals(className) && "abortResourcePackRecovery".equals(element.getMethodName())) {
                return className + "." + element.getMethodName();
            }
        }
        return null;
    }
}
