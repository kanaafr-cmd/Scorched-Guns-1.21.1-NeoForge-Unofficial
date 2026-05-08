package top.ribs.scguns.init;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;
import top.ribs.scguns.block.*;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class ModBlocks {

    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, Reference.MOD_ID);
    public static final DeferredHolder<Block, Block> TEMPORARY_LIGHT = REGISTER.register("temporary_light",
            TemporaryLightBlock::new);
    public static final DeferredHolder<Block, Block> GUN_SHELF = register("gun_shelf",
            () -> new GunShelfBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(0.5F)));
    public static final DeferredHolder<Block, Block> MOB_TRAP = register("mob_trap",
            () -> new MobTrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(12.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> AMMO_BOX = register("ammo_box",
            () -> new AmmoBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .strength(2.5F)));
    public static final DeferredHolder<Block, Block> ADVANCED_COMPOSTER = register("advanced_composter",
            () -> new AdvancedComposterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COMPOSTER)
                    .strength(0.5F)));
    public static final DeferredHolder<Block, Block> POWDER_KEG = register("powder_keg",
            () -> new PowderKegBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .strength(0.5F)));
    public static final DeferredHolder<Block, Block> NITRO_KEG = register("nitro_keg",
            () -> new NitroKegBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL)
                    .strength(0.5F)));
    public static final DeferredHolder<Block, Block> CRYONITER = register("cryoniter",
            () -> new CryoniterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> THERMOLITH = register("thermolith",
            () -> new ThermolithBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> BASIC_TURRET = register("basic_turret",
            () -> new BasicTurretBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> SHOTGUN_TURRET = register("shotgun_turret",
            () -> new ShotgunTurretBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> CHARGED_AMETHYST_RELAY = register("charged_amethyst_relay",
            () -> new ChargedAmethystRelayBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.REPEATER)
                    .strength(0.0F)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> AUTO_TURRET = register("auto_turret",
            () -> new AutoTurretBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> ENEMY_TURRET = register("enemy_turret",
            () -> new EnemyTurretBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(8.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> POLAR_GENERATOR = register("polar_generator",
            () -> new PolarGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.COW_BELL)
                    .strength(3.0F)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> LIGHTNING_ROD_CONNECTOR = register("lightning_rod_connector",
            () -> new LightningRodConnectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHTNING_ROD)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> LIGHTNING_BATTERY = register("lightning_battery",
            () -> new LightningBattery(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.COW_BELL)
                    .strength(3.0F)
                    .noOcclusion()
                    .lightLevel((state) -> state.getValue(LightningBattery.CHARGED) ? 15 : 0)));

    public static final DeferredHolder<Block, Block> MACERATOR = register("macerator",
            () -> new MaceratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.HAT)
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> POWERED_MACERATOR = register("powered_macerator",
            () -> new PoweredMaceratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.HAT)
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> MECHANICAL_PRESS = register("mechanical_press",
            () -> new MechanicalPressBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BANJO)
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> POWERED_MECHANICAL_PRESS = register("powered_mechanical_press",
            () -> new PoweredMechanicalPressBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BANJO)
                    .strength(3.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> GUN_BENCH = register("gun_bench",
            () -> new GunBenchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CRAFTING_TABLE).instrument(NoteBlockInstrument.GUITAR)
                    .requiresCorrectToolForDrops()
                    .strength(2.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> ANTHRALITE_BLOCK = register("anthralite_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> TREATED_IRON_BLOCK = register("treated_iron_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> TREATED_BRASS_BLOCK = register("treated_brass_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> VEHEMENT_COAL_BLOCK = registerBurnable("vehement_coal_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_BLOCK).instrument(NoteBlockInstrument.DIDGERIDOO)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)), 43200);

    public static final DeferredHolder<Block, Block> PLASMA_BLOCK = registerBurnable("plasma_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .lightLevel((state) -> 15)
                    .strength(3.0F)), 16200);


    public static final DeferredHolder<Block, Block> DIAMOND_STEEL_BLOCK = register("diamond_steel_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> RAW_ANTHRALITE_BLOCK = register("raw_anthralite_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> ANTHRALITE_ORE = register("anthralite_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 0), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredHolder<Block, Block> DEEPSLATE_ANTHRALITE_ORE = register("deepslate_anthralite_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 0), BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_IRON_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final DeferredHolder<Block, Block> SULFUR_BLOCK = registerBurnable("sulfur_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)), 7200);
    public static final DeferredHolder<Block, Block> ANCIENT_BRASS_BLOCK = register("ancient_brass_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_COPPER_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)));

    public static final DeferredHolder<Block, Block> SKIBIDI = register("skibidi",
            () -> new BasicDirectionalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAW_COPPER_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> FELIX_MEMORIAL = register("felix_memorial",
            () -> new MemorialBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ANDESITE)
                    .requiresCorrectToolForDrops()
                    .strength(4.0F)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> TURRET_TARGETING_BLOCK = register("turret_targeting_module",
            () -> new TurretTargetingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> PLAYER_TURRET_TARGETING_BLOCK = register("player_turret_targeting_module",
            () -> new PlayerTurretTargetingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> HOSTILE_TURRET_TARGETING_BLOCK = register("hostile_turret_targeting_module",
            () -> new HostileTurretTargetingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> FIRE_RATE_TURRET_MODULE = register("fire_rate_turret_module",
            () -> new FireRateModuleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> DAMAGE_TURRET_MODULE = register("damage_turret_module",
            () -> new DamageModuleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> RANGE_TURRET_MODULE = register("range_turret_module",
            () -> new RangeModuleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> SHELL_CATCHER_TURRET_MODULE = register("shell_catcher_turret_module",
            () -> new ShellCatcherModuleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> AMMO_TURRET_MODULE = register("ammo_turret_module",
            () -> new AmmoModuleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .randomTicks()
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> RICH_PHOSPHORITE = register("rich_phosphorite",
            () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> SULFUR_ORE = register("sulfur_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
    public static final DeferredHolder<Block, Block> DEEPSLATE_SULFUR_ORE = register("deepslate_sulfur_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 2), BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE).mapColor(MapColor.DEEPSLATE).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final DeferredHolder<Block, Block> NETHER_SULFUR_ORE = register("nether_sulfur_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_GOLD_ORE)));
    public static final DeferredHolder<Block, Block> VEHEMENT_COAL_ORE = register("vehement_coal_ore",
            () -> new DropExperienceBlock(UniformInt.of(0, 1), BlockBehaviour.Properties.of().mapColor(MapColor.NETHER).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F).sound(SoundType.NETHER_GOLD_ORE)));

    public static final DeferredHolder<Block, Block> NITER_LAYER = register("niter",
            () -> new NiterLayerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));

    public static final DeferredHolder<Block, Block> SULFUR_LAYER = register("sulfur",
            () -> new SulfurLayerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));

    public static final DeferredHolder<Block, Block> NITER_BLOCK = register("niter_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SAND)));

    public static final DeferredHolder<Block, Block> PENETRATOR = register("penetrator",
            () -> new PenetratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .strength(1.0F), 10));

    public static final DeferredHolder<Block, Block> PLASMA_LANTERN = register("plasma_lantern",
            () -> new LanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LANTERN)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .strength(3.0F)
                    .lightLevel((state) -> 15)));

    public static final DeferredHolder<Block, Block> GEOTHERMAL_VENT = register("geothermal_vent",
            () -> new GeothermalVentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F)
                    .noOcclusion()
                    .randomTicks()));
    public static final DeferredHolder<Block, Block> SULFUR_VENT = register("sulfur_vent",
            () -> new SulfurVentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)
                    .requiresCorrectToolForDrops()
                    .strength(12.0F)
                    .noOcclusion()
                    .randomTicks()));


    public static final DeferredHolder<Block, Block> VENT_COLLECTOR = register("vent_collector",
            () -> new VentCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).noOcclusion()
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .noOcclusion()
                    .randomTicks()));


    public static final DeferredHolder<Block, Block> NITER_GLASS = register("niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> WHITE_NITER_GLASS = register("white_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> ORANGE_NITER_GLASS = register("orange_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> MAGENTA_NITER_GLASS = register("magenta_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> LIGHT_BLUE_NITER_GLASS = register("light_blue_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> YELLOW_NITER_GLASS = register("yellow_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> LIME_NITER_GLASS = register("lime_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> PINK_NITER_GLASS = register("pink_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> GRAY_NITER_GLASS = register("gray_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> LIGHT_GRAY_NITER_GLASS = register("light_gray_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> CYAN_NITER_GLASS = register("cyan_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> PURPLE_NITER_GLASS = register("purple_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> BLUE_NITER_GLASS = register("blue_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> BROWN_NITER_GLASS = register("brown_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> GREEN_NITER_GLASS = register("green_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> RED_NITER_GLASS = register("red_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> BLACK_NITER_GLASS = register("black_niter_glass",
            () -> new NiterGlassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final DeferredHolder<Block, Block> RAW_PHOSPHOR_BLOCK = register("raw_phosphor_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> PHOSPHORITE = register("phosphorite",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(1.5F)));
    public static final DeferredHolder<Block, Block> SMOOTH_PHOSPHORITE = register("smooth_phosphorite",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> POLISHED_PHOSPHORITE = register("polished_phosphorite",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> PHOSPHORITE_BRICKS = register("phosphorite_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> CRACKED_PHOSPHORITE_BRICKS = register("cracked_phosphorite_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> PHOSPHORITE_BRICK_SLAB = register("phosphorite_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> PHOSPHORITE_BRICK_STAIRS = register("phosphorite_brick_stairs",
            () -> new StairBlock(ModBlocks.PHOSPHORITE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));
    public static final DeferredHolder<Block, Block> PHOSPHORITE_BRICK_WALL = register("phosphorite_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F)));

    public static final DeferredHolder<Block, Block> ASGHARIAN_PILLAR = register("asgharian_pillar",
            () -> new AsgharianPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));
    public static final DeferredHolder<Block, Block> ASGHARIAN_BRICKS = register("asgharian_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));


    public static final DeferredHolder<Block, Block> CRACKED_ASGHARIAN_BRICKS = register("cracked_asgharian_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));
    public static final DeferredHolder<Block, Block> ASGHARIAN_BRICK_SLAB = register("asgharian_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));
    public static final DeferredHolder<Block, Block> ASGHARIAN_BRICK_STAIRS = register("asgharian_brick_stairs",
            () -> new StairBlock(ModBlocks.ASGHARIAN_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));
    public static final DeferredHolder<Block, Block> ASGHARIAN_BRICK_WALL = register("asgharian_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.PLING)
                    .strength(4.0F)));

    public static final DeferredHolder<Block, Block> TREATED_BRASS_PLATES = register("treated_brass_plates",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.FLUTE)
                    .strength(3.0F)));
    public static final DeferredHolder<Block, Block> CUT_TREATED_BRASS = register("cut_treated_brass",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.FLUTE)
                    .strength(2.5F)));
    public static final DeferredHolder<Block, Block> TREATED_IRON_GRATE = register("treated_iron_grate",
            () -> new TreatedIronGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .requiresCorrectToolForDrops().instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .strength(3.0F)
                    .noOcclusion()));

    public static final DeferredHolder<Block, Block> SANDBAG = register("sandbag",
            () -> new SandbagBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)
                    .strength(0.5F)
                    .noOcclusion()));
    public static final DeferredHolder<Block, Block> SUPPLY_CRATE = register("supply_crate",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .strength(1.0F)));


    private static <T extends Block> DeferredHolder<Block, T> register(String id, Supplier<T> blockSupplier) {
        return register(id, blockSupplier, block1 -> new BlockItem(block1, new Item.Properties()));
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String id, Supplier<T> blockSupplier, @Nullable Function<T, BlockItem> supplier) {
        DeferredHolder<Block, T> registryObject = REGISTER.register(id, blockSupplier);
        if (supplier != null) {
            ModItems.REGISTER.register(id, () -> supplier.apply(registryObject.get()));
        }
        return registryObject;
    }

    private static <T extends Block> DeferredHolder<Block, T> registerBurnable(String id, Supplier<T> blockSupplier, int burnTime) {
        return register(id, blockSupplier, block -> new BlockItem(block, new Item.Properties()) {
            @Override
            public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                return burnTime;
            }
        });
    }
}
