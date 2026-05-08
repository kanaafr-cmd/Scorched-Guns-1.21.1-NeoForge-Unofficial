package top.ribs.scguns.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import top.ribs.scguns.Reference;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.MinecraftForge;
import top.ribs.scguns.compat.net.neoforged.neoforge.event.TickEvent;

@EventBusSubscriber(modid = Reference.MOD_ID)
public final class TickEventBridge {
    private TickEventBridge() {
    }

    @SubscribeEvent
    public static void onClientTickPre(net.neoforged.neoforge.client.event.ClientTickEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.START));
    }

    @SubscribeEvent
    public static void onClientTickPost(net.neoforged.neoforge.client.event.ClientTickEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.ClientTickEvent(TickEvent.Phase.END));
    }

    @SubscribeEvent
    public static void onRenderFramePre(net.neoforged.neoforge.client.event.RenderFrameEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.START, 0.0F));
    }

    @SubscribeEvent
    public static void onRenderFramePost(net.neoforged.neoforge.client.event.RenderFrameEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.RenderTickEvent(TickEvent.Phase.END, 0.0F));
    }

    @SubscribeEvent
    public static void onPlayerTickPre(net.neoforged.neoforge.event.tick.PlayerTickEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.START, side(event), event.getEntity()));
    }

    @SubscribeEvent
    public static void onPlayerTickPost(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.PlayerTickEvent(TickEvent.Phase.END, side(event), event.getEntity()));
    }

    @SubscribeEvent
    public static void onServerTickPre(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.ServerTickEvent(TickEvent.Phase.START));
    }

    @SubscribeEvent
    public static void onServerTickPost(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new TickEvent.ServerTickEvent(TickEvent.Phase.END));
    }

    private static LogicalSide side(net.neoforged.neoforge.event.tick.PlayerTickEvent event) {
        return event.getEntity().level().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }
}
