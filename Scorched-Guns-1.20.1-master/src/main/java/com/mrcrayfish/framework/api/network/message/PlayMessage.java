package com.mrcrayfish.framework.api.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import net.minecraft.network.FriendlyByteBuf;

public abstract class PlayMessage<T extends PlayMessage<T>> {
    public abstract void encode(T message, FriendlyByteBuf buffer);

    public abstract T decode(FriendlyByteBuf buffer);

    public abstract void handle(T message, MessageContext context);
}
