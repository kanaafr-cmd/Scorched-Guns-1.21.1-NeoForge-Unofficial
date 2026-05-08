package top.ribs.scguns.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

public final class ItemStackNbtHelper {
    public static final HolderLookup.Provider EMPTY_REGISTRIES = HolderLookup.Provider.create(Stream.empty());
    private static final Map<ItemStack, LiveCompoundTag> TAG_CACHE = new WeakHashMap<>();

    private ItemStackNbtHelper() {
    }

    public static CompoundTag getTag(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        LiveCompoundTag cached = TAG_CACHE.get(stack);
        if (cached != null) {
            return cached;
        }
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (data.isEmpty()) {
            return null;
        }
        LiveCompoundTag tag = new LiveCompoundTag(stack, data.copyTag());
        TAG_CACHE.put(stack, tag);
        return tag;
    }

    public static CompoundTag getOrCreateTag(ItemStack stack) {
        if (stack.isEmpty()) {
            return new CompoundTag();
        }
        LiveCompoundTag cached = TAG_CACHE.get(stack);
        if (cached != null) {
            return cached;
        }
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        LiveCompoundTag tag = new LiveCompoundTag(stack, data.copyTag());
        TAG_CACHE.put(stack, tag);
        if (data.isEmpty()) {
            setTag(stack, tag);
        }
        return tag;
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        if (tag instanceof LiveCompoundTag live && live.stack == stack) {
            TAG_CACHE.put(stack, live);
        } else if (!stack.isEmpty()) {
            TAG_CACHE.put(stack, new LiveCompoundTag(stack, tag));
        }
    }

    public static void removeTag(ItemStack stack) {
        stack.remove(DataComponents.CUSTOM_DATA);
        TAG_CACHE.remove(stack);
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

    private static final class LiveCompoundTag extends CompoundTag {
        private final ItemStack stack;
        private boolean syncing;

        private LiveCompoundTag(ItemStack stack, CompoundTag source) {
            this.stack = stack;
            super.merge(source);
        }

        private void sync() {
            if (!this.syncing && !this.stack.isEmpty()) {
                this.syncing = true;
                ItemStackNbtHelper.setTag(this.stack, this);
                this.syncing = false;
            }
        }

        @Override
        public Tag put(String key, Tag value) {
            Tag previous = super.put(key, value);
            this.sync();
            return previous;
        }

        @Override
        public void putByte(String key, byte value) {
            super.putByte(key, value);
            this.sync();
        }

        @Override
        public void putShort(String key, short value) {
            super.putShort(key, value);
            this.sync();
        }

        @Override
        public void putInt(String key, int value) {
            super.putInt(key, value);
            this.sync();
        }

        @Override
        public void putLong(String key, long value) {
            super.putLong(key, value);
            this.sync();
        }

        @Override
        public void putFloat(String key, float value) {
            super.putFloat(key, value);
            this.sync();
        }

        @Override
        public void putDouble(String key, double value) {
            super.putDouble(key, value);
            this.sync();
        }

        @Override
        public void putString(String key, String value) {
            super.putString(key, value);
            this.sync();
        }

        @Override
        public void putBoolean(String key, boolean value) {
            super.putBoolean(key, value);
            this.sync();
        }

        @Override
        public void remove(String key) {
            super.remove(key);
            this.sync();
        }

        @Override
        public CompoundTag merge(CompoundTag source) {
            CompoundTag merged = super.merge(source);
            this.sync();
            return merged;
        }
    }
}
