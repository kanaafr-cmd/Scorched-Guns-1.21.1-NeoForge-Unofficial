package top.ribs.scguns.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Config;
import top.ribs.scguns.client.GunItemStackRenderer;
import top.ribs.scguns.client.KeyBinds;
import top.ribs.scguns.common.*;
import top.ribs.scguns.init.ModEnchantments;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.init.ModTags;
import top.ribs.scguns.item.attachment.IAttachment;
import top.ribs.scguns.util.GunEnchantmentHelper;
import top.ribs.scguns.util.GunModifierHelper;
import top.ribs.scguns.util.ItemStackNbtHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class GunItem extends Item implements IColored, IMeta {
    private final WeakHashMap<CompoundTag, Gun> modifiedGunCache = new WeakHashMap<>();
    private Gun gun = new Gun();

    public GunItem(Item.Properties properties) {
        super(properties);
    }

    public void setGun(NetworkGunManager.Supplier supplier) {
        this.gun = supplier.getGun();
    }

    public Gun getGun() {
        return this.gun;
    }
    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Gun modifiedGun = this.getModifiedGun(stack);

        ResourceLocation advantage = modifiedGun.getProjectile().getAdvantage();
        float baseDamage = modifiedGun.getProjectile().getDamage();
        baseDamage = GunModifierHelper.getModifiedProjectileDamage(stack, baseDamage);
        baseDamage = GunEnchantmentHelper.getAcceleratorDamage(stack, baseDamage);
        baseDamage = GunEnchantmentHelper.getHeavyShotDamage(stack, baseDamage);

        baseDamage *= Config.COMMON.gameplay.globalDamageMultiplier.get().floatValue();
        String additionalDamageText = "";
        Level worldIn = Minecraft.getInstance().level;
        CompoundTag tagCompound = ItemStackNbtHelper.getTag(stack);
        if (tagCompound != null) {
            if (tagCompound.contains("AdditionalDamage", Tag.TAG_ANY_NUMERIC)) {
                float additionalDamage = tagCompound.getFloat("AdditionalDamage");
                additionalDamage += GunModifierHelper.getAdditionalDamage(stack, false);

                if (additionalDamage > 0) {
                    additionalDamageText = ChatFormatting.GREEN + " +" + String.format(Locale.ROOT, "%.1f", additionalDamage);
                } else if (additionalDamage < 0) {
                    additionalDamageText = ChatFormatting.RED + " " + String.format(Locale.ROOT, "%.1f", additionalDamage);
                }
            }
        }

        tooltip.add(Component.translatable("info.scguns.damage")
                .append(": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format(Locale.ROOT, "%.1f", baseDamage) + additionalDamageText).withStyle(ChatFormatting.WHITE)));
        float baseArmorPen = modifiedGun.getProjectile().getArmorPen();
        float puncturingPen = GunEnchantmentHelper.getPuncturingArmorBypass(stack);
        float totalArmorPen = baseArmorPen + puncturingPen;

        if (totalArmorPen > 0) {
            String armorPenText = String.format(Locale.ROOT, "%.1f", totalArmorPen);
            tooltip.add(Component.translatable("info.scguns.armor_penetration")
                    .append(": ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(armorPenText).withStyle(ChatFormatting.YELLOW)));
        }

        if (!advantage.equals(ModTags.Entities.NONE.location())) {
            tooltip.add(Component.translatable("info.scguns.advantage").withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable("advantage." + advantage).withStyle(ChatFormatting.GOLD)));
        }
        String fireMode = modifiedGun.getGeneral().getFireMode().id().toString();
        tooltip.add(Component.translatable("info.scguns.fire_mode").withStyle(ChatFormatting.GRAY)
                .append(Component.translatable("fire_mode." + fireMode).withStyle(ChatFormatting.WHITE)));

        Item ammo = modifiedGun.getProjectile().getItem();
        Item reloadItem = modifiedGun.getReloads().getReloadItem();
        if (modifiedGun.getReloads().getReloadType() == ReloadType.SINGLE_ITEM) {
            ammo = reloadItem;
        }

        if (tagCompound != null) {
            if (tagCompound.getBoolean("IgnoreAmmo")) {
                tooltip.add(Component.translatable("info.scguns.ignore_ammo").withStyle(ChatFormatting.AQUA));
            } else {
                int ammoCount = tagCompound.getInt("AmmoCount");
                tooltip.add(Component.translatable("info.scguns.ammo")
                        .append(": ").withStyle(ChatFormatting.GRAY)
                        .append(Component.literal(ammoCount + "/" + GunModifierHelper.getModifiedAmmoCapacity(stack, modifiedGun)).withStyle(ChatFormatting.WHITE)));
            }
        }
        float totalMeleeDamage = getTotalMeleeDamage(stack);
        if (totalMeleeDamage > 0) {
            String meleeDamageText = (totalMeleeDamage % 1.0 == 0) ? String.format("%d", (int) totalMeleeDamage) : String.format("%.1f", totalMeleeDamage);
            tooltip.add(Component.translatable("info.scguns.melee_damage")
                    .append(": ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(meleeDamageText).withStyle(ChatFormatting.WHITE)));
        }
        ResourceLocation effectLocation = modifiedGun.getProjectile().getImpactEffect();
        if (effectLocation != null) {
            MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectLocation);
            if (effect != null) {
                tooltip.add(Component.translatable("info.scguns.impact_effect").withStyle(ChatFormatting.GRAY)
                        .append(": ")
                        .append(Component.translatable(effect.getDescriptionId()).withStyle(ChatFormatting.BLUE)));
            }
        }

        Gun.WeaponType weaponType = modifiedGun.getGeneral().getWeaponType();
        if (weaponType != null) {
            String weaponTypeKey = "desc.scguns." + weaponType.name().toLowerCase();
            tooltip.add(Component.translatable("info.scguns.weapon_type").withStyle(ChatFormatting.GRAY)
                    .append(": ")
                    .append(Component.translatable(weaponTypeKey).withStyle(ChatFormatting.AQUA)));
        }
        if (ammo != null) {
            tooltip.add(Component.translatable("info.scguns.ammo_type", Component.translatable(ammo.getDescriptionId()).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.GRAY));
        }

        if (modifiedGun.getGeneral().allowsAmmoChange() && !modifiedGun.getGeneral().getAvailableAmmoTypes().isEmpty()) {
            if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
                tooltip.add(Component.translatable("info.scguns.available_ammo_types").withStyle(ChatFormatting.GRAY));

                int currentIndex = modifiedGun.getGeneral().getCurrentAmmoTypeIndex();
                List<String> ammoTypes = modifiedGun.getGeneral().getAvailableAmmoTypes();

                for (int i = 0; i < ammoTypes.size(); i++) {
                    String ammoType = ammoTypes.get(i);
                    ResourceLocation ammoLocation = ResourceLocation.parse(
                            ammoType.contains(":") ? ammoType : top.ribs.scguns.Reference.MOD_ID + ":" + ammoType
                    );
                    Item ammoItem = BuiltInRegistries.ITEM.get(ammoLocation);

                    if (ammoItem != null) {
                        Component ammoName = Component.translatable(ammoItem.getDescriptionId());
                        if (i == currentIndex) {
                            tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.YELLOW)
                                    .append(ammoName.copy().withStyle(ChatFormatting.YELLOW))
                                    .append(Component.literal(" ✓").withStyle(ChatFormatting.GREEN)));
                        } else {
                            tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY)
                                    .append(ammoName.copy().withStyle(ChatFormatting.WHITE)));
                        }
                    }
                }
            } else {
                tooltip.add(Component.translatable("info.scguns.ammo_swap_available").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
            }
        }
        tooltip.add(Component.translatable("info.scguns.attachment_help", KeyBinds.KEY_ATTACHMENTS.getTranslatedKeyMessage().getString().toUpperCase(Locale.ENGLISH)).withStyle(ChatFormatting.YELLOW));
    }



    public float getBayonetAdditionalDamage(ItemStack gunStack) {
        float additionalDamage = 0.0F;
        for (IAttachment.Type type : IAttachment.Type.values()) {
            ItemStack attachmentStack = Gun.getAttachment(type, gunStack);
            if (attachmentStack != null && attachmentStack.getItem() instanceof BayonetItem) {
                additionalDamage += ((BayonetItem) attachmentStack.getItem()).getAdditionalDamage();
            }
        }
        return additionalDamage;
    }

    public float getTotalMeleeDamage(ItemStack stack) {
        Gun gun = this.getModifiedGun(stack);
        float baseMeleeDamage = gun.getGeneral().getMeleeDamage();
        float bayonetDamage = getBayonetAdditionalDamage(stack);
        return baseMeleeDamage + bayonetDamage;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        Gun gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
        return gun.getGeneral().getRate() * 4;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.isDamaged();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        ItemStackNbtHelper.getOrCreateTag(stack);
        this.getModifiedGun(stack);
        return Math.round(13.0F - (float) stack.getDamageValue() * 13.0F / (float) this.getMaxDamage(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        if (stack.getDamageValue() >= (stack.getMaxDamage() - 1)) {
            return 0x808080;
        }

        if (stack.getDamageValue() >= (stack.getMaxDamage() / 1.5)) {
            return Objects.requireNonNull(ChatFormatting.RED.getColor());
        }
        float stackMaxDamage = this.getMaxDamage(stack);
        float f = Math.max(0.0F, (stackMaxDamage - (float) stack.getDamageValue()) / stackMaxDamage);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    public Gun getModifiedGun(ItemStack stack) {
        CompoundTag tagCompound = ItemStackNbtHelper.getTag(stack);
        if (tagCompound != null && tagCompound.contains("Gun", Tag.TAG_COMPOUND)) {
            return this.modifiedGunCache.computeIfAbsent(tagCompound, item -> {
                if (tagCompound.getBoolean("Custom")) {
                    return Gun.create(tagCompound.getCompound("Gun"));
                } else {
                    Gun gunCopy = this.gun.copy();
                    gunCopy.deserializeNBT(tagCompound.getCompound("Gun"));
                    return gunCopy;
                }
            });
        }
        return this.gun;
    }

    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.getMaxStackSize(stack) == 1;
    }

    @Override
    public int getEnchantmentValue() {
        return 13;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new GunItemStackRenderer();
            }
        });
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
        return pRepair.is(ModItems.REPAIR_KIT.get());
    }

    public ItemStack getAttachment(ItemStack heldItem, IAttachment.Type type) {
        return Gun.getAttachment(type, heldItem);
    }

    public boolean hasBayonet(ItemStack gunStack) {
        if (isBuiltInBayonetGun(gunStack)) {
            return true;
        }
        for (IAttachment.Type type : IAttachment.Type.values()) {
            ItemStack attachmentStack = Gun.getAttachment(type, gunStack);
            if (attachmentStack != null && attachmentStack.getItem() instanceof BayonetItem) {
                return true;
            }
        }
        return false;
    }
    public boolean isBuiltInBayonetGun(ItemStack gunStack) {
        return gunStack.is(ModTags.Items.BUILT_IN_BAYONET);
    }


    public boolean hasExtendedBarrel(ItemStack gunStack) {
        for (IAttachment.Type type : IAttachment.Type.values()) {
            ItemStack attachmentStack = Gun.getAttachment(type, gunStack);
            if (attachmentStack != null && attachmentStack.getItem() instanceof ExtendedBarrelItem) {
                return true;
            }
        }
        return false;
    }
    public boolean isOneHandedCarbineCandidate(ItemStack gunStack) {
        return gunStack.is(ModTags.Items.ONE_HANDED_CARBINE);
    }

    public boolean isOneHandedCarbineActive(ItemStack gunStack) {
        return isOneHandedCarbineCandidate(gunStack) &&
                !Gun.hasExtendedBarrel(gunStack) &&
                !Gun.hasStock(gunStack);
    }

    public void onAttachmentChanged(ItemStack stack) {
        CompoundTag tag = ItemStackNbtHelper.getOrCreateTag(stack);
        tag.putBoolean("AttachmentChanged", true);
        ItemStackNbtHelper.setTag(stack, tag);
    }

    public int getBayonetBanzaiLevel(ItemStack gunStack) {
        for (IAttachment.Type type : IAttachment.Type.values()) {
            ItemStack attachmentStack = Gun.getAttachment(type, gunStack);
            if (attachmentStack != null && attachmentStack.getItem() instanceof BayonetItem) {
                return 0;
            }
        }
        return 0;
    }

    public Gun getGunProperties() {
        return this.gun;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        if (stack.is(ModTags.Items.MINING_GUN)) {
            return true;
        }
        return super.isBookEnchantable(stack, book);
    }

}
