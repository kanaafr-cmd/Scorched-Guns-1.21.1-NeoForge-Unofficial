package dev.codex.cbctabfix.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.InsertableLinkedOpenCustomHashSet;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BuildCreativeModeTabContentsEvent.class)
public abstract class BuildCreativeModeTabContentsEventMixin {
    private static final String CREATE_BIG_CANNONS_NAMESPACE = "createbigcannons";

    @Shadow
    @Final
    private InsertableLinkedOpenCustomHashSet<ItemStack> parentEntries;

    @Shadow
    @Final
    private InsertableLinkedOpenCustomHashSet<ItemStack> searchEntries;

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void cbcCreativeTabDuplicateFix$acceptCbcEntriesOnce(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (!isCreateBigCannonsItem(newEntry)) {
            return;
        }

        if (newEntry.getCount() != 1) {
            return;
        }

        if (visibility == CreativeModeTab.TabVisibility.PARENT_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
            addIfMissing(parentEntries, newEntry);
        }

        if (visibility == CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY || visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
            addIfMissing(searchEntries, newEntry);
        }

        ci.cancel();
    }

    private static boolean isCreateBigCannonsItem(ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return CREATE_BIG_CANNONS_NAMESPACE.equals(itemId.getNamespace());
    }

    private static void addIfMissing(InsertableLinkedOpenCustomHashSet<ItemStack> entries, ItemStack stack) {
        if (!entries.contains(stack)) {
            entries.add(stack);
        }
    }
}
