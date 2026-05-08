package top.ribs.scguns.compat.net.neoforged.neoforge.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface IEntityAdditionalSpawnData {
    void writeSpawnData(FriendlyByteBuf buffer);

    void readSpawnData(FriendlyByteBuf buffer);
}
