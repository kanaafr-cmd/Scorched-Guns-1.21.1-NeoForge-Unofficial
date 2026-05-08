package top.ribs.scguns.item;

import top.ribs.scguns.util.ItemStackNbtHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.util.LazyOptional;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.NotNull;
import top.ribs.scguns.Config;
import top.ribs.scguns.item.ammo_boxes.CreativeAmmoBoxItem;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AmmoBoxItem extends Item {
    public static final String TAG_ITEMS = "Items";
    private static final int BAR_COLOR = 0x6666FF;

    public AmmoBoxItem(Item.Properties properties) {
        super(properties);
    }


    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if (stack.getCount() == 1 && action == ClickAction.SECONDARY) {
            ItemStack itemStackInSlot = slot.getItem();
            if (itemStackInSlot.isEmpty()) {
                this.playRemoveOneSound(player);
                removeOne(stack).ifPresent(removedStack -> add(stack, slot.safeInsert(removedStack)));
            } else if (itemStackInSlot.is(ItemTags.create(getAmmoTag()))) {
                int maxInsertCount = getMaxItemCount(stack) - getTotalItemCount(stack);
                int itemsToInsert = Math.min(itemStackInSlot.getCount(), maxInsertCount);
                int insertedItems = add(stack, slot.safeTake(itemStackInSlot.getCount(), itemsToInsert, player));
                if (insertedItems > 0) {
                    this.playInsertSound(player);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack otherStack, Slot slot, ClickAction action, Player player, SlotAccess slotAccess) {
        if (stack.getCount() != 1) {
            return false;
        } else if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (otherStack.isEmpty()) {
                removeOne(stack).ifPresent(removedStack -> {
                    this.playRemoveOneSound(player);
                    slotAccess.set(removedStack);
                });
            } else if (otherStack.is(ItemTags.create(getAmmoTag()))) {
                int maxInsertCount = getMaxItemCount(stack) - getTotalItemCount(stack);
                int itemsToInsert = Math.min(otherStack.getCount(), maxInsertCount);
                int insertedItems = add(stack, otherStack.copyWithCount(itemsToInsert));
                if (insertedItems > 0) {
                    this.playInsertSound(player);
                    otherStack.shrink(insertedItems);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getTotalItemCount(stack) > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.min(1 + 12 * getTotalItemCount(stack) / getMaxItemCount(stack), 13);
    }

    protected abstract ResourceLocation getAmmoTag();

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public static int add(ItemStack pouchStack, ItemStack insertedStack) {
        if (!insertedStack.isEmpty() && insertedStack.is(ItemTags.create(((AmmoBoxItem)pouchStack.getItem()).getAmmoTag()))) {
            CompoundTag compoundTag = ItemStackNbtHelper.getOrCreateTag(pouchStack);
            if (!compoundTag.contains(TAG_ITEMS)) {
                compoundTag.put(TAG_ITEMS, new ListTag());
            }

            int maxItemCount = getMaxItemCount(pouchStack);
            int itemsToInsert = Math.min(insertedStack.getCount(), maxItemCount - getTotalItemCount(pouchStack));

            if (itemsToInsert == 0) {
                return 0;
            }

            ListTag listTag = compoundTag.getList(TAG_ITEMS, 10);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag itemTag = listTag.getCompound(i);
                ItemStack existingStack = ItemStackNbtHelper.parse(itemTag);
                if (ItemStack.isSameItemSameComponents(existingStack, insertedStack)) {
                    int remainingSpace = Math.min(existingStack.getMaxStackSize() - existingStack.getCount(), itemsToInsert);
                    existingStack.grow(remainingSpace);
                    itemsToInsert -= remainingSpace;
                    itemTag = ItemStackNbtHelper.save(existingStack);
                    listTag.set(i, itemTag);
                    if (itemsToInsert <= 0) {
                        break;
                    }
                }
            }

            while (itemsToInsert > 0) {
                int countToInsert = Math.min(insertedStack.getMaxStackSize(), itemsToInsert);
                ItemStack newItemStack = insertedStack.copyWithCount(countToInsert);
                CompoundTag newItemTag = ItemStackNbtHelper.save(newItemStack);
                listTag.add(newItemTag);
                itemsToInsert -= countToInsert;
            }

            compoundTag.put(TAG_ITEMS, listTag);
            return insertedStack.getCount() - itemsToInsert;
        } else {
            return 0;
        }
    }

    public static int getTotalItemCount(ItemStack stack) {
        return getContents(stack).mapToInt(ItemStack::getCount).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack stack) {
        CompoundTag compoundTag = ItemStackNbtHelper.getOrCreateTag(stack);
        if (!compoundTag.contains(TAG_ITEMS)) {
            return Optional.empty();
        } else {
            ListTag listTag = compoundTag.getList(TAG_ITEMS, 10);
            if (listTag.isEmpty()) {
                return Optional.empty();
            } else {
                CompoundTag itemTag = listTag.getCompound(0);
                ItemStack itemStack = ItemStackNbtHelper.parse(itemTag);
                listTag.remove(0);
                if (listTag.isEmpty()) {
                    CompoundTag stackTag = ItemStackNbtHelper.getOrCreateTag(stack);
                    stackTag.remove(TAG_ITEMS);
                    ItemStackNbtHelper.setTag(stack, stackTag);
                }
                return Optional.of(itemStack);
            }
        }
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        getContents(stack).forEach(nonNullList::add);
        return Optional.of(new BundleTooltip(new BundleContents(nonNullList)));
    }
    @Override
    public void onDestroyed(@NotNull ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, getContents(itemEntity.getItem()).toList());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced) {
        // tooltipComponents.add(Component.translatable("item.scguns.ammo_pouch.fullness").withStyle(ChatFormatting.GRAY));
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    protected void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    public static int getMaxItemCount(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof AmmoBoxItem) {
            double multiplier = Config.COMMON.gameplay.ammoBoxCapacityMultiplier.get();
            return (int) (((AmmoBoxItem) item).getBaseMaxItemCount() * multiplier);
        }
        return 256;
    }

    protected abstract int getBaseMaxItemCount();



    public static Stream<ItemStack> getContents(ItemStack stack) {
        if (stack.getItem() instanceof CreativeAmmoBoxItem) {
            TagKey<Item> ammoTag = ItemTags.create(((CreativeAmmoBoxItem)stack.getItem()).getAmmoTag());
            return BuiltInRegistries.ITEM.stream().toList().stream()
                    .filter(item -> item.builtInRegistryHolder().is(ammoTag))
                    .map(item -> new ItemStack(item, Integer.MAX_VALUE));
        }
        CompoundTag compoundTag = ItemStackNbtHelper.getTag(stack);
        if (compoundTag == null) {
            return Stream.empty();
        }
        ListTag listTag = compoundTag.getList(TAG_ITEMS, 10);
        return listTag.stream().map(CompoundTag.class::cast).map(ItemStackNbtHelper::parse);
    }
}
