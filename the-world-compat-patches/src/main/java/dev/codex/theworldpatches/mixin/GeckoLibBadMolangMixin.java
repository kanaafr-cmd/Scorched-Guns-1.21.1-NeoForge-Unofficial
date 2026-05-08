package dev.codex.theworldpatches.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "software.bernie.geckolib.loading.math.MathParser", remap = false)
public abstract class GeckoLibBadMolangMixin {

    @Inject(
            method = "compileExpression(Ljava/lang/String;)Lsoftware/bernie/geckolib/loading/math/MathValue;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void theworld$zeroInvalidBareMinus(String expression, CallbackInfoReturnable<Object> cir) {
        if (expression == null || !"-".equals(expression.trim())) {
            return;
        }

        try {
            Class<?> constantClass = Class.forName("software.bernie.geckolib.loading.math.value.Constant");
            cir.setReturnValue(constantClass.getConstructor(double.class).newInstance(0.0D));
        } catch (ReflectiveOperationException ignored) {
            // Let GeckoLib throw its original parse error if its internals change.
        }
    }
}
