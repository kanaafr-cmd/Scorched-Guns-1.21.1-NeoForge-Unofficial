package com.mrcrayfish.controllable.client.binding;

public interface IBindingContext {
    boolean isActive();

    boolean conflicts(IBindingContext other);
}
