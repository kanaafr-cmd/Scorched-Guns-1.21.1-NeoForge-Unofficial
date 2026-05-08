package com.mrcrayfish.framework.api.data.login;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

public interface ILoginData {
    void writeData(FriendlyByteBuf buffer);

    Optional<String> readData(FriendlyByteBuf buffer);
}
