package top.ribs.scguns.event;

import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import top.ribs.scguns.Reference;
import top.ribs.scguns.init.ModCommands;

@EventBusSubscriber(modid = Reference.MOD_ID)
public class ModCommandsRegister {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
}