package dev.codex.apothiccompat;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

@Mod(ApothicBossHealthCompat.MOD_ID)
public final class ApothicBossHealthCompat {
    public static final String MOD_ID = "apothic_boss_health_compat";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String INVADER_KEY = "apoth.boss";
    private static final String INVADER_RARITY_KEY = "apoth.boss.rarity";
    private static final String ELITE_KEY = "apoth.miniboss";
    private static final String ELITE_PLAYER_KEY = "apoth.miniboss.player";
    private static final String PATCHED_KEY = MOD_ID + ".healed_to_max";

    public ApothicBossHealthCompat() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Loaded {}. Apothic Spawners present: {}", MOD_ID, ModList.get().isLoaded("apothic_spawners"));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFinalizeSpawn(FinalizeSpawnEvent event) {
        healMarkedEntityTree(event.getEntity(), "finalize_spawn");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            healMarkedEntityTree(event.getEntity(), "entity_join_level");
        }
    }

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        // Apotheosis initializes elites during EntityJoinLevelEvent at LOWEST priority.
        // This short delayed pass catches any entity modified after our join callback.
        for (Entity entity : ((ServerLevel) event.getLevel()).getAllEntities()) {
            if (entity.tickCount <= 3) {
                healMarkedEntityTree(entity, "early_level_tick");
            }
        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        logInvaderDataMapStatus(event);
        LOGGER.info(
            "{} bundled Apotheosis resources are available: modded_invader_entries={}, elite_entity_tag={}",
            MOD_ID,
            "data/apothic_boss_health_compat/apothic_invaders/*",
            "data/apothic_boss_health_compat/tags/entity_type/apothic_compat_elite_targets.json");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void logInvaderDataMapStatus(ServerStartedEvent event) {
        try {
            Object rawType = Class.forName("dev.shadowsoffire.apotheosis.Apoth$DataMaps")
                .getField("INVADER_SPAWN_RULES")
                .get(null);
            DataMapType dataMapType = (DataMapType) rawType;
            var dimensions = event.getServer().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE);
            boolean overworld = hasInvaderRules(dimensions, dataMapType, BuiltinDimensionTypes.OVERWORLD);
            boolean nether = hasInvaderRules(dimensions, dataMapType, BuiltinDimensionTypes.NETHER);
            boolean end = hasInvaderRules(dimensions, dataMapType, BuiltinDimensionTypes.END);

            LOGGER.info(
                "{} Apotheosis invader_spawn_rules data map loaded: overworld={}, nether={}, end={}",
                MOD_ID,
                overworld,
                nether,
                end);
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            LOGGER.warn("{} could not reflect Apotheosis invader_spawn_rules data map status.", MOD_ID, ex);
        }
    }

    private static boolean hasInvaderRules(net.minecraft.core.Registry<DimensionType> dimensions, DataMapType<DimensionType, ?> dataMapType, ResourceKey<DimensionType> dimension) {
        return dimensions.getData(dataMapType, dimension) != null;
    }

    private static void healMarkedEntityTree(Entity root, String source) {
        root.getSelfAndPassengers().forEach(entity -> {
            if (entity instanceof LivingEntity living) {
                healIfMarked(living, source);
            }
        });
    }

    private static void healIfMarked(LivingEntity living, String source) {
        CompoundTag data = living.getPersistentData();
        Marker marker = marker(data);
        if (marker == Marker.NONE) {
            return;
        }

        float oldHealth = living.getHealth();
        float maxHealth = living.getMaxHealth();
        boolean healed = false;

        if (oldHealth > 0.0F && oldHealth + 0.001F < maxHealth && !data.getBoolean(PATCHED_KEY)) {
            living.setHealth(maxHealth);
            data.putBoolean(PATCHED_KEY, true);
            healed = true;
        }

        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(living.getType());
        LOGGER.info(
            "Detected Apothic {} entity {} via {}: old_health={}, max_health={}, healed_to_full={}, rarity={}, elite_player={}",
            marker.logName,
            entityId,
            source,
            oldHealth,
            maxHealth,
            healed,
            data.getString(INVADER_RARITY_KEY),
            data.getString(ELITE_PLAYER_KEY));
    }

    private static Marker marker(CompoundTag data) {
        if (data.getBoolean(INVADER_KEY) || data.contains(INVADER_RARITY_KEY)) {
            return Marker.INVADER;
        }
        if (data.getBoolean(ELITE_KEY) || data.contains(ELITE_PLAYER_KEY)) {
            return Marker.ELITE;
        }
        return Marker.NONE;
    }

    private enum Marker {
        NONE("none"),
        INVADER("invader"),
        ELITE("elite");

        private final String logName;

        Marker(String logName) {
            this.logName = logName;
        }
    }
}
