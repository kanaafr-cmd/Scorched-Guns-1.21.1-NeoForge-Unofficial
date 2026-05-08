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
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.Reference;

import java.lang.reflect.Field;
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
		if (pool == null) {
			ScorchedGuns.LOGGER.warn("Unable to inject village building {}; template pool {} was not found", nbtPieceRL, poolRL);
			return;
		}

		ResourceLocation emptyProcessor = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
		Holder<StructureProcessorList> processorHolder = processorListRegistry.getHolderOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, emptyProcessor));
		SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL, processorHolder).apply(StructureTemplatePool.Projection.RIGID);

		try {
			Field templatesField = StructureTemplatePool.class.getDeclaredField("templates");
			templatesField.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<StructurePoolElement> templates = (List<StructurePoolElement>) templatesField.get(pool);
			for (int i = 0; i < weight; i++) {
				templates.add(piece);
			}

			Field rawTemplatesField = StructureTemplatePool.class.getDeclaredField("rawTemplates");
			rawTemplatesField.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<Pair<StructurePoolElement, Integer>> rawTemplates = (List<Pair<StructurePoolElement, Integer>>) rawTemplatesField.get(pool);
			try {
				rawTemplates.add(Pair.of(piece, weight));
			} catch (UnsupportedOperationException ignored) {
				ScorchedGuns.LOGGER.debug("Template pool {} has immutable raw template metadata; injected {} into runtime templates only", poolRL, nbtPieceRL);
			}
		} catch (ReflectiveOperationException | ClassCastException e) {
			ScorchedGuns.LOGGER.error("Unable to inject village building {} into pool {}", nbtPieceRL, poolRL, e);
		}
	}
}
