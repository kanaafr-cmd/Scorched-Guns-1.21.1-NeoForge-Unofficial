package top.ribs.scguns.common;

import top.ribs.scguns.annotation.Ignored;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.util.INBTSerializable;
import top.ribs.scguns.util.ItemStackNbtHelper;

/**
 * Author: MrCrayfish
 */
public class CustomGun implements INBTSerializable<CompoundTag>
{
    @Ignored
    public ItemStack model;
    public Gun gun;

    public ItemStack getModel()
    {
        return this.model;
    }

    public Gun getGun()
    {
        return this.gun;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries)
    {
        CompoundTag compound = new CompoundTag();
        compound.put("Model", ItemStackNbtHelper.save(this.model, registries));
        compound.put("Gun", this.gun.serializeNBT());
        return compound;
    }

    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag compound)
    {
        this.model = ItemStack.parseOptional(registries, compound.getCompound("Model"));
        this.gun = Gun.create(compound.getCompound("Gun"));
    }

    public CompoundTag serializeNBT()
    {
        return this.serializeNBT(ItemStackNbtHelper.EMPTY_REGISTRIES);
    }

    public void deserializeNBT(CompoundTag compound)
    {
        this.deserializeNBT(ItemStackNbtHelper.EMPTY_REGISTRIES, compound);
    }
}
