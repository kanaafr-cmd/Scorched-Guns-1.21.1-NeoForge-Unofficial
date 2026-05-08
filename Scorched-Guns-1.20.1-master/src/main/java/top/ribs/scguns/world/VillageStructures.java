package top.ribs.scguns.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import top.ribs.scguns.Reference;

import java.sql.Ref;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class VillageStructures {
	public static void addNewVillageBuilding(final ServerAboutToStartEvent event) {
		Registry<StructureTemplatePool> templatePools = event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).get();
		Registry<StructureProcessorList> processorLists = event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).get();
		VillageStructures.addBuildingToPool(templatePools, processorLists, ResourceLocation.parse("minecraft:village/plains/houses"), Reference.MOD_ID + ":village/houses/plains_gunsmith_house", 8);
      VillageStructures.addBuildingToPool(templatePools, processorLists, ResourceLocation.parse("minecraft:village/snowy/houses"), Reference.MOD_ID + ":village/houses/snowy_gunsmith_house", 8);
      VillageStructures.addBuildingToPool(templatePools, processorLists, ResourceLocation.parse("minecraft:village/savanna/houses"), Reference.MOD_ID + ":village/houses/savanna_gunsmith_house", 8);
		VillageStructures.addBuildingToPool(templatePools, processorLists, ResourceLocation.parse("minecraft:village/desert/houses"), Reference.MOD_ID + ":village/houses/desert_gunsmith_house", 8);
      VillageStructures.addBuildingToPool(templatePools, processorLists, ResourceLocation.parse("minecraft:village/taiga/houses"), Reference.MOD_ID + ":village/houses/taiga_gunsmith_house", 8);
	}

	public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, ResourceLocation poolRL, String nbtPieceRL, int weight) {
		StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
		ResourceLocation emptyProcessor = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
		Holder<StructureProcessorList> processorHolder = processorListRegistry.getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, emptyProcessor));

		SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL, processorHolder).apply(StructureTemplatePool.Projection.RIGID);

        List<StructurePoolElement> templates = getTemplates(pool);
		for (int i = 0; i < weight; i++) {
            assert pool != null;
            templates.add(piece);
		}

		List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(getRawTemplates(pool));
		listOfPieceEntries.add(new Pair<>(piece, weight));
		setRawTemplates(pool, listOfPieceEntries);
	}

    @SuppressWarnings("unchecked")
    private static List<StructurePoolElement> getTemplates(StructureTemplatePool pool) {
        return (List<StructurePoolElement>) getField(pool, "templates");
    }

    @SuppressWarnings("unchecked")
    private static List<Pair<StructurePoolElement, Integer>> getRawTemplates(StructureTemplatePool pool) {
        return (List<Pair<StructurePoolElement, Integer>>) getField(pool, "rawTemplates");
    }

    private static Object getField(StructureTemplatePool pool, String name) {
        try {
            Field field = StructureTemplatePool.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(pool);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to access structure pool field " + name, e);
        }
    }

    private static void setRawTemplates(StructureTemplatePool pool, List<Pair<StructurePoolElement, Integer>> entries) {
        try {
            Field field = StructureTemplatePool.class.getDeclaredField("rawTemplates");
            field.setAccessible(true);
            field.set(pool, entries);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to update village structure pool", e);
        }
    }
}
