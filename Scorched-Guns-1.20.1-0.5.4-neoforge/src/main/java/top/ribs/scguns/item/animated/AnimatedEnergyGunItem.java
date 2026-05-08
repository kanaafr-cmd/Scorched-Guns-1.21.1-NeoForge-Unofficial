package top.ribs.scguns.item.animated;


import top.ribs.scguns.util.ItemStackNbtHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities.Capability;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import top.ribs.scguns.interfaces.IEnergyGun;

import javax.annotation.Nullable;
import java.util.List;

public class AnimatedEnergyGunItem extends AnimatedGunItem implements IEnergyGun {
    private final int capacity;

    public AnimatedEnergyGunItem(Properties properties, String path,
                                 SoundEvent reloadSoundMagOut,
                                 SoundEvent reloadSoundMagIn,
                                 SoundEvent reloadSoundEnd,
                                 SoundEvent boltPullSound,
                                 SoundEvent boltReleaseSound,
                                 int capacity) {
        super(properties, path, reloadSoundMagOut, reloadSoundMagIn, reloadSoundEnd, boltPullSound, boltReleaseSound);
        this.capacity = capacity;
    }

    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> new ItemEnergyStorage(stack, capacity));

            @Override
            public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
                return cap == ForgeCapabilities.ENERGY ? energy.cast() : LazyOptional.empty();
            }
        };
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getEnergyStored(stack) / getMaxEnergyStored(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FF00; // Green color for the energy bar
    }

    public int getEnergyStored(ItemStack stack) {
        CompoundTag tag = ItemStackNbtHelper.getTag(stack);
        return tag != null && tag.contains("Energy", Tag.TAG_INT) ? tag.getInt("Energy") : 0;
    }

    public int getMaxEnergyStored(ItemStack stack) {
        return capacity;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        int energyStored = getEnergyStored(stack);
        int maxEnergy = getMaxEnergyStored(stack);

        tooltip.add(Component.translatable("info.scguns.energy")
                .append(": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(energyStored + " / " + maxEnergy + " FE").withStyle(ChatFormatting.GREEN)));
    }

    public static class ItemEnergyStorage implements IEnergyStorage {
        private final ItemStack stack;
        private final int capacity;
        private int energy;

        public ItemEnergyStorage(ItemStack stack, int capacity) {
            this.stack = stack;
            this.capacity = capacity;
            this.energy = loadEnergyFromNBT();
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int energyReceived = Math.min(capacity - energy, maxReceive);
            if (!simulate) {
                energy += energyReceived;
                updateEnergyTag();
            }
            return energyReceived;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energyExtracted = Math.min(energy, maxExtract);
            if (!simulate) {
                energy -= energyExtracted;
                updateEnergyTag();
            }
            return energyExtracted;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return capacity;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        private void updateEnergyTag() {
            CompoundTag tag = ItemStackNbtHelper.getOrCreateTag(stack);
            tag.putInt("Energy", energy);
            ItemStackNbtHelper.setTag(stack, tag);
        }

        private int loadEnergyFromNBT() {
            CompoundTag tag = ItemStackNbtHelper.getTag(stack);
            return tag != null && tag.contains("Energy", Tag.TAG_INT) ? tag.getInt("Energy") : 0;
        }
    }
}
