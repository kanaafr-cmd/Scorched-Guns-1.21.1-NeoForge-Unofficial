package top.ribs.scguns.init;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.ribs.scguns.Reference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public final class ModPointOfInterestTypes
{
    public static final DeferredRegister<PoiType> REGISTER = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Reference.MOD_ID);


    private static DeferredHolder<PoiType, PoiType> register(String name, DeferredHolder<Block, Block> block, int maxFreeTickets) {
        List<DeferredHolder<Block, Block>> blocks = new ArrayList<>();
        blocks.add(block);
        return register(name, blocks, maxFreeTickets);
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, Supplier<List<DeferredHolder<Block, Block>>> supplier, int maxFreeTickets) {
        return register(name, supplier.get(), maxFreeTickets);
    }

    private static DeferredHolder<PoiType, PoiType> register(String name, List<DeferredHolder<Block, Block>> blocks, int maxFreeTickets) {
        return REGISTER.register(name, () -> {
            Set<BlockState> blockStates = new HashSet<>();
            for (DeferredHolder<Block, Block> block : blocks) {
                blockStates.addAll(block.get().getStateDefinition().getPossibleStates());
            }
            return new PoiType(blockStates, maxFreeTickets, 1);
        });
    }
}
