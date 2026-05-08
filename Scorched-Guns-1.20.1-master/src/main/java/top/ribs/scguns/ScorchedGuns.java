package top.ribs.scguns;

import com.mrcrayfish.framework.api.FrameworkAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import top.ribs.scguns.compat.net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.ribs.scguns.attributes.SCAttributes;
import top.ribs.scguns.client.CustomGunManager;
import top.ribs.scguns.client.handler.*;
import top.ribs.scguns.client.screen.*;
import top.ribs.scguns.common.BoundingBoxManager;
import top.ribs.scguns.common.NetworkGunManager;
import top.ribs.scguns.common.ProjectileManager;
import top.ribs.scguns.common.exosuit.ExoSuitUpgradeManager;
import top.ribs.scguns.compat.CreateModCondition;
import top.ribs.scguns.compat.FarmersDelightModCondition;
import top.ribs.scguns.compat.IEModCondition;
import top.ribs.scguns.event.SculkHordeEvents;
import top.ribs.scguns.config.MerchantTradeConfig;
import top.ribs.scguns.config.ProjectileAdvantageConfig;
import top.ribs.scguns.entity.config.ConfigLoader;
import top.ribs.scguns.entity.projectile.*;
import top.ribs.scguns.entity.throwable.GrenadeEntity;
import top.ribs.scguns.event.*;
import top.ribs.scguns.init.ModBlockEntities;
import top.ribs.scguns.client.ClientHandler;
import top.ribs.scguns.entity.config.CogMinionConfig;
import top.ribs.scguns.init.*;
import top.ribs.scguns.network.PacketHandler;
import top.ribs.scguns.world.VillageStructures;
import java.io.IOException;
import java.io.InputStream;

import static top.ribs.scguns.Reference.MOD_ID;
import static top.ribs.scguns.compat.CompatManager.SCULK_HORDE_LOADED;

@Mod(MOD_ID)
public class ScorchedGuns {
    public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>> CONDITION_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, MOD_ID);

    public static final String MODID = "scguns";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static CogMinionConfig COG_MINION_CONFIG = new CogMinionConfig();
    public static boolean backpackedLoaded;
    public static boolean curiosLoaded;
    public static boolean controllableLoaded;
    public static boolean playerReviveLoaded;
    public static boolean createLoaded;
    public static boolean farmersDelightLoaded;
    public static boolean mekanismLoaded;
    public static boolean ieLoaded;
    public static boolean valkyrienSkiesLoaded;
    public static boolean marjLoaded;
    private static boolean useEnergyGuns = false;

    public ScorchedGuns(IEventBus modEventBus, ModContainer modContainer) {
        // Common setup
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.clientSpec);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        Config.GunScalingConfig.setup(modContainer);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
        IEventBus bus = modEventBus;
        modEventBus.addListener(this::onConfigLoad);
        CONDITION_SERIALIZERS.register("create_mod_loaded", () -> CreateModCondition.CODEC);
        CONDITION_SERIALIZERS.register("farmers_delight_mod_loaded", () -> FarmersDelightModCondition.CODEC);
        CONDITION_SERIALIZERS.register("immersiveengineering_mod_loaded", () -> IEModCondition.CODEC);
        CONDITION_SERIALIZERS.register(bus);
        ModItems.REGISTER.register(bus);

        SCAttributes.ATTRIBUTES.register(modEventBus);

        initializeModDependencies();
        ModItems.registerItems();
        // Register other mod features
        MinecraftForge.EVENT_BUS.addListener(VillageStructures::addNewVillageBuilding);
        ModCreativeModeTabs.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModBlocks.REGISTER.register(bus);
        ModContainers.REGISTER.register(bus);
        ModEffects.REGISTER.register(bus);
        ModMenuTypes.register(bus);
        ModEntities.REGISTER.register(bus);
        ModParticleTypes.REGISTER.register(bus);
        ModRecipes.register(bus);
        ModSounds.REGISTER.register(bus);
        ModVillagers.register(bus);
        ModFeatures.register(bus);
        ModLootModifiers.LOOT_MODIFIERS.register(bus);
        ModPointOfInterestTypes.REGISTER.register(bus);

        ModStructures.REGISTRY.register(bus);
        bus.addListener(this::onCommonSetup);

        // Ensure client-specific code is run only on the client side
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientHandler.registerClientHandlers(bus);
            MinecraftForge.EVENT_BUS.register(HUDRenderHandler.class);
            MinecraftForge.EVENT_BUS.register(InspectHandler.get());
            MinecraftForge.EVENT_BUS.register(BeamHandler.class);
        });

        // Register the mod itself to the event bus
        MinecraftForge.EVENT_BUS.register(this);
        //MinecraftForge.EVENT_BUS.register(ArmorBoostEventHandler.class);
       /// MinecraftForge.EVENT_BUS.register(ArmorRemoveEventHandler.class);
        MinecraftForge.EVENT_BUS.register(WeaponMovementEventHandler.class);
        MinecraftForge.EVENT_BUS.register(OceanWeaponEventHandler.class);
        MinecraftForge.EVENT_BUS.register(PiglinWeaponEventHandler.class);
        MinecraftForge.EVENT_BUS.register(MerchantTradeConfig.class);
        MinecraftForge.EVENT_BUS.register(ProjectileAdvantageConfig.class);


        if (SCULK_HORDE_LOADED) {
            MinecraftForge.EVENT_BUS.register(SculkHordeEvents.class);
        }
    }
    private void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            // Only call RecoilHandler on client side
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                RecoilHandler.get().updateConfig();
            });
        }
    }
    private void initializeModDependencies() {
        // Check for optional mod dependencies
        valkyrienSkiesLoaded = ModList.get().isLoaded("valkyrienskies");
        controllableLoaded = ModList.get().isLoaded("controllable");
        backpackedLoaded = ModList.get().isLoaded("backpacked");
        curiosLoaded = ModList.get().isLoaded("curios");
        playerReviveLoaded = ModList.get().isLoaded("playerrevive");
        createLoaded = ModList.get().isLoaded("create");
        farmersDelightLoaded = ModList.get().isLoaded("farmersdelight");
        ieLoaded = ModList.get().isLoaded("immersiveengineering");
        mekanismLoaded = ModList.get().isLoaded("mekanism");
        marjLoaded = ModList.get().isLoaded("majruszsdifficulty");
    }
    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ExoSuitUpgradeManager());
    }

    public static boolean shouldUseEnergyGuns() {
        return !createLoaded || useEnergyGuns;
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.init();
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.AIMING);
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.RELOADING);
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.SHOOTING);
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.BURSTCOUNT);
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.ONBURSTCOOLDOWN);
            FrameworkAPI.registerSyncedDataKey(ModSyncedDataKeys.MELEE);
            ProjectileManager.getInstance().registerFactory(ModItems.POWDER_AND_BALL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.GRAPESHOT.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.COMPACT_COPPER_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.HOG_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new HogRoundProjectileEntity(ModEntities.HOG_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.STANDARD_COPPER_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.COMPACT_ADVANCED_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.RAMROD_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new RamrodProjectileEntity(ModEntities.RAMROD_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.ADVANCED_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new AdvancedRoundProjectileEntity(ModEntities.ADVANCED_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SHATTER_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ShatterRoundProjectileEntity(ModEntities.SHATTER_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.KRAHG_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new KrahgRoundProjectileEntity(ModEntities.KRAHG_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.BEOWULF_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new BeowulfProjectileEntity(ModEntities.BEOWULF_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.GIBBS_ROUND.get(), (worldIn, entity, weapon, item, modifiedGun) -> new GibbsRoundProjectileEntity(ModEntities.GIBBS_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SHOTGUN_SHELL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.BEARPACK_SHELL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new BearPackShellProjectileEntity(ModEntities.BEARPACK_SHELL_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.BLAZE_FUEL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new FireRoundEntity(ModEntities.FIRE_ROUND_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SCULK_CELL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new SculkCellEntity(ModEntities.SCULK_CELL.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SHOCK_CELL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new LightningProjectileEntity(ModEntities.PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SHULKSHOT.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ShulkshotProjectileEntity(ModEntities.SHULKSHOT.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.ENERGY_CELL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new PlasmaProjectileEntity(ModEntities.PLASMA_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.OSBORNE_SLUG.get(), (worldIn, entity, weapon, item, modifiedGun) -> new OsborneSlugProjectileEntity(ModEntities.OSBORNE_SLUG_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(Items.BLAZE_ROD, (worldIn, entity, weapon, item, modifiedGun) -> new BlazeRodProjectileEntity(ModEntities.BLAZE_ROD_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SYRINGE.get(), (worldIn, entity, weapon, item, modifiedGun) -> new SyringeProjectileEntity(ModEntities.SYRINGE_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.STANDARD_BULLET.get(), (worldIn, entity, weapon, item, modifiedGun) -> new BasicBulletProjectileEntity(ModEntities.BASIC_BULLET_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.ADVANCED_BULLET.get(), (worldIn, entity, weapon, item, modifiedGun) -> new HardenedBulletProjectileEntity(ModEntities.HARDENED_BULLET_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.BUCKSHOT.get(), (worldIn, entity, weapon, item, modifiedGun) -> new BuckshotProjectileEntity(ModEntities.BUCKSHOT_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.NITRO_BUCKSHOT.get(), (worldIn, entity, weapon, item, modifiedGun) -> new BuckshotProjectileEntity(ModEntities.BUCKSHOT_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.SHOTBALL.get(), (worldIn, entity, weapon, item, modifiedGun) -> new ShotballProjectileEntity(ModEntities.SHOTBALL_PROJECTILE.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.ROCKET.get(), (worldIn, entity, weapon, item, modifiedGun) -> new RocketEntity(ModEntities.ROCKET.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.MICROJET.get(), (worldIn, entity, weapon, item, modifiedGun) -> new MicroJetEntity(ModEntities.MICROJET.get(), worldIn, entity, weapon, item, modifiedGun));
            ProjectileManager.getInstance().registerFactory(ModItems.GRENADE.get(), (worldIn, entity, weapon, item, modifiedGun) -> new GrenadeEntity(ModEntities.GRENADE.get(), worldIn, entity, weapon, item, modifiedGun));
            useEnergyGuns = Config.COMMON.gameplay.forceEnergyGuns.get();

          if (Config.COMMON.gameplay.improvedHitboxes.get()) {
                MinecraftForge.EVENT_BUS.register(new BoundingBoxManager());
            }
            try {
                InputStream inputStream = ScorchedGuns.class.getClassLoader().getResourceAsStream("data/scguns/entity/cog_minion_item.json");
                if (inputStream != null) {
                    COG_MINION_CONFIG = ConfigLoader.loadCogMinionConfig(inputStream);
                    inputStream.close();
                } else {
                    LOGGER.error("Could not find Cog Minion config");
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load Cog Minion config", e);
            }
        });
    }
    public static boolean isDebugging() {
        return false;
    }
}
