package top.ribs.scguns.compat.net.neoforged.neoforge.common.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class LazyOptional<T> {
    private final Supplier<T> supplier;

    private LazyOptional(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyOptional<T> of(Supplier<T> supplier) {
        return new LazyOptional<>(supplier);
    }

    public static <T> LazyOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(() -> value);
    }

    public static <T> LazyOptional<T> empty() {
        return new LazyOptional<>(() -> null);
    }

    public boolean isPresent() {
        return get() != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T value = get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    public T orElse(T other) {
        T value = get();
        return value != null ? value : other;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        T value = get();
        if (value != null) {
            return value;
        }
        throw exceptionSupplier.get();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        T value = get();
        return value == null ? Optional.empty() : Optional.ofNullable(mapper.apply(value));
    }

    @SuppressWarnings("unchecked")
    public <U> LazyOptional<U> cast() {
        return new LazyOptional<>(() -> (U) get());
    }

    public void invalidate() {
    }

    private T get() {
        return supplier.get();
    }
}
