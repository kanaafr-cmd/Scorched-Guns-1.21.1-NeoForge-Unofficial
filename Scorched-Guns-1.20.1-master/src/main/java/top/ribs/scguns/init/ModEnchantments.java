package top.ribs.scguns.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;

public class ModEnchantments {
    public static final DeferredHolder<Enchantment, Enchantment> QUICK_HANDS = holder("quick_hands");
    public static final DeferredHolder<Enchantment, Enchantment> TRIGGER_FINGER = holder("trigger_finger");
    public static final DeferredHolder<Enchantment, Enchantment> LIGHTWEIGHT = holder("lightweight");
    public static final DeferredHolder<Enchantment, Enchantment> COLLATERAL = holder("collateral");
    public static final DeferredHolder<Enchantment, Enchantment> RECLAIMED = holder("reclaimed");
    public static final DeferredHolder<Enchantment, Enchantment> ACCELERATOR = holder("accelerator");
    public static final DeferredHolder<Enchantment, Enchantment> PUNCTURING = holder("puncturing");
    public static final DeferredHolder<Enchantment, Enchantment> SHELL_CATCHER = holder("shell_catcher");
    public static final DeferredHolder<Enchantment, Enchantment> BANZAI = holder("banzai");
    public static final DeferredHolder<Enchantment, Enchantment> HEAVY_SHOT = holder("heavy_shot");
    public static final DeferredHolder<Enchantment, Enchantment> ELEMENTAL_POP = holder("elemental_pop");
    public static final DeferredHolder<Enchantment, Enchantment> WATER_PROOF = holder("waterproof");
    public static final DeferredHolder<Enchantment, Enchantment> HOT_BARREL = holder("hot_barrel");
    public static final DeferredHolder<Enchantment, Enchantment> GUN_RUST = holder("gun_rust");
    public static final DeferredHolder<Enchantment, Enchantment> CORRODED = holder("corroded");

    private static DeferredHolder<Enchantment, Enchantment> holder(String name) {
        return DeferredHolder.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, name));
    }
}
