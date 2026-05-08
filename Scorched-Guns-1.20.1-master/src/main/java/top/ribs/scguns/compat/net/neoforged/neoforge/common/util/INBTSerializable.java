package top.ribs.scguns.compat.net.neoforged.neoforge.common.util;

public interface INBTSerializable<T> {
    default T serializeNBT() {
        return null;
    }

    default void deserializeNBT(T nbt) {
    }
}
