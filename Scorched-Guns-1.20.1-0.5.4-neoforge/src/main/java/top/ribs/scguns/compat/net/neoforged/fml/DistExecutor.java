package top.ribs.scguns.compat.net.neoforged.fml;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public final class DistExecutor {
    private DistExecutor() {
    }

    public static void unsafeRunWhenOn(Dist dist, Supplier<Runnable> toRun) {
        if (FMLEnvironment.dist == dist) {
            toRun.get().run();
        }
    }

    public static <T> T unsafeCallWhenOn(Dist dist, Supplier<Supplier<T>> toRun) {
        return FMLEnvironment.dist == dist ? toRun.get().get() : null;
    }
}
