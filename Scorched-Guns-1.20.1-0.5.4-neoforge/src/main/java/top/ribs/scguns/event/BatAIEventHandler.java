package top.ribs.scguns.event;

import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import top.ribs.scguns.Reference;
import top.ribs.scguns.entity.ai.BatGlowberryGoal;

@EventBusSubscriber(modid = Reference.MOD_ID)
public class BatAIEventHandler {

    @SubscribeEvent
    public static void onBatSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Bat bat) {
            if (!event.getLevel().isClientSide()) {
                bat.goalSelector.addGoal(2, new BatGlowberryGoal(bat, 0.6, 8));
            }
        }
    }
}