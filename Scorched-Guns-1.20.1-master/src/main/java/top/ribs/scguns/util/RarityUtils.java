package top.ribs.scguns.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.Map;


public class RarityUtils {

    private static final Map<ResourceLocation, Rarity> ITEM_RARITY_MAP = new HashMap<>();

    static {
        // Add your custom items and their rarities here
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "floundergat"), Constants.OCEANIC);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "marlin"), Constants.OCEANIC);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "bomb_lance"), Constants.OCEANIC);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "ocean_blueprint"), Constants.OCEANIC);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "sequoia"), Constants.OCEANIC);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "hullbreaker"), Constants.OCEANIC);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "super_shotgun"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "blasphemy"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "freyr"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "pyroclastic_flow"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "vulcanic_repeater"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "piglin_blueprint"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "mangalitsa"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "trotters"), Constants.PIGLISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "hog_round"), Constants.PIGLISH);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "whispers"), Constants.DEEP_DARK);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "echoes_2"), Constants.DEEP_DARK);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "sculk_resonator"), Constants.DEEP_DARK);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "deep_dark_blueprint"), Constants.DEEP_DARK);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "forlorn_hope"), Constants.DEEP_DARK);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "sculk_cell"), Constants.DEEP_DARK);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "end_blueprint"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "lone_wonder"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "raygun"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "dark_matter"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "shellurker"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "carapice"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "weevil"), Constants.ENDISH);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "shulkshot"), Constants.ENDISH);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "scorched_blueprint"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "scorched_ingot"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "earths_corpse"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "rat_king_and_queen"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "locust"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "newborn_cyst"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "astella"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "flayed_god"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "nervepinch"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "prima_materia"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "terra_incognita"), Constants.SCORCHED);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "advanced_exo_suit_core"), Constants.SCORCHED);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "the_pact"), Constants.SCORCHED);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "felix_memorial"), Constants.PIGLISH);

        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "ultra_knight_hawk"), Constants.BIZARRE);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "big_bore"), Constants.BIZARRE);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "osborne_slug"), Constants.BIZARRE);
        ITEM_RARITY_MAP.put(ResourceLocation.fromNamespaceAndPath("scguns", "ribs_glory"), Constants.PIGLISH);

    }



    public static Rarity GetRarityFromResourceLocation(ResourceLocation location, Rarity oldRarity) {
        return ITEM_RARITY_MAP.getOrDefault(location, oldRarity);
    }

    public static Rarity GetRarityFromItem(Item item, Rarity old) {
        var items = BuiltInRegistries.ITEM;
        if (items.containsValue(item)) {
            return GetRarityFromResourceLocation(items.getKey(item), old);
        }
        return old;
    }
}
