package top.ribs.scguns.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.stream.Stream;

public final class ItemStackNbtHelper {
    public static final HolderLookup.Provider EMPTY_REGISTRIES = HolderLookup.Provider.create(Stream.empty());

    private ItemStackNbtHelper() {
    }

    public static CompoundTag getTag(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.isEmpty() ? null : data.copyTag();
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        CompoundTag tag = getTag(stack);
        return tag == null ? new CompoundTag() : tag;
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static void removeTag(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
    }

    public static CompoundTag save(ItemStack stack, HolderLookup.Provider registries) {
        return (CompoundTag) stack.save(registries);
    }

    public static CompoundTag save(ItemStack stack) {
        return save(stack, EMPTY_REGISTRIES);
    }

    public static ItemStack parse(CompoundTag tag, HolderLookup.Provider registries) {
        return ItemStack.parseOptional(registries, tag);
    }

    public static ItemStack parse(CompoundTag tag) {
        return parse(tag, EMPTY_REGISTRIES);
    }
}
