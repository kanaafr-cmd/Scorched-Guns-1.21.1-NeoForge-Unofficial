package top.ribs.scguns.init;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import top.ribs.scguns.Reference;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ModArmorMaterials {
    public static final Holder<ArmorMaterial> ADRIEN = material("adrien", 22, new int[]{3, 6, 6, 4}, 8,
            SoundEvents.ARMOR_EQUIP_IRON, 0.5f, 0.1f, () -> Ingredient.of(ModItems.TREATED_IRON_INGOT.get()));
    public static final Holder<ArmorMaterial> ANTHRALITE = material("anthralite", 32, new int[]{2, 4, 3, 2}, 12,
            SoundEvents.ARMOR_EQUIP_GOLD, 1.0f, 0.05f, () -> Ingredient.of(ModItems.ANTHRALITE_INGOT.get()));
    public static final Holder<ArmorMaterial> DIAMOND_STEEL = material("diamond_steel", 36, new int[]{3, 6, 5, 3}, 16,
            SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f, 0.05f, () -> Ingredient.of(ModItems.DIAMOND_STEEL_INGOT.get()));
    public static final Holder<ArmorMaterial> TREATED_BRASS = material("treated_brass", 30, new int[]{4, 6, 5, 4}, 10,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.2f, () -> Ingredient.of(ModItems.TREATED_BRASS_INGOT.get()));
    public static final Holder<ArmorMaterial> ANCIENT_BRASS = material("ancient_brass", 16, new int[]{3, 5, 4, 3}, 10,
            SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.15f, () -> Ingredient.of(ModItems.ANCIENT_BRASS.get()));
    public static final Holder<ArmorMaterial> EXO_SUIT = material("exo_suit", 200, new int[]{1, 1, 1, 1}, 6,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 0.0f, 0.0f, () -> Ingredient.of(ModItems.TREATED_IRON_INGOT.get()));

    private static final int[] BASE_DURABILITY = {8, 12, 12, 9};

    private ModArmorMaterials() {
    }

    private static Holder<ArmorMaterial> material(String name, int durabilityMultiplier, int[] protectionAmounts,
                                                  int enchantmentValue, Holder<SoundEvent> equipSound, float toughness,
                                                  float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        return Holder.direct(new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.BOOTS, protectionAmounts[0],
                        ArmorItem.Type.LEGGINGS, protectionAmounts[1],
                        ArmorItem.Type.CHESTPLATE, protectionAmounts[2],
                        ArmorItem.Type.HELMET, protectionAmounts[3],
                        ArmorItem.Type.BODY, protectionAmounts[0]
                ),
                enchantmentValue,
                equipSound,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, name))),
                toughness,
                knockbackResistance
        ));
    }

    public static int durabilityForType(int durabilityMultiplier, ArmorItem.Type type) {
        return BASE_DURABILITY[type.ordinal()] * durabilityMultiplier;
    }
}
