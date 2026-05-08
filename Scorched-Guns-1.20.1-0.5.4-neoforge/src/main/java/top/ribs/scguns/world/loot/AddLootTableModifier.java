package top.ribs.scguns.world.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.storage.loot.LootTable.createStackSplitter;

/**
 * Credits to Commoble for this implementation!
 */
public class AddLootTableModifier extends LootModifier
{
	public static final MapCodec<AddLootTableModifier> CODEC =
			RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
					.and(ResourceLocation.CODEC.fieldOf("lootTable").forGetter((m) -> m.lootTable))
					.apply(inst, AddLootTableModifier::new));

	private final ResourceLocation lootTable;

	protected AddLootTableModifier(LootItemCondition[] conditionsIn, ResourceLocation lootTable) {
		super(conditionsIn);
		this.lootTable = lootTable;
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
			ResourceKey<LootTable> tableKey = ResourceKey.create(Registries.LOOT_TABLE, this.lootTable);
			LootTable extraTable = context.getResolver().get(Registries.LOOT_TABLE, tableKey).map(Holder::value).orElse(null);
			if (extraTable == null) {
				return generatedLoot;
			}
			extraTable.getRandomItemsRaw(context, createStackSplitter(context.getLevel(), generatedLoot::add));

		return generatedLoot;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
