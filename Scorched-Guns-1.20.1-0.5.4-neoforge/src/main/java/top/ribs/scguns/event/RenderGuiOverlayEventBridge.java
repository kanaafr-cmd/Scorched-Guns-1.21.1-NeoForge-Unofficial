package top.ribs.scguns.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import top.ribs.scguns.Reference;
import top.ribs.scguns.compat.net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import top.ribs.scguns.compat.net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.MinecraftForge;

@EventBusSubscriber(modid = Reference.MOD_ID)
public final class RenderGuiOverlayEventBridge {
    private RenderGuiOverlayEventBridge() {
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(net.neoforged.neoforge.client.event.RenderGuiLayerEvent.Pre event) {
        if (!VanillaGuiLayers.CROSSHAIR.equals(event.getName())) {
            return;
        }

        RenderGuiOverlayEvent.Pre bridged = new RenderGuiOverlayEvent.Pre(
                event.getGuiGraphics(),
                event.getPartialTick(),
                VanillaGuiOverlay.CROSSHAIR.type()
        );
        MinecraftForge.EVENT_BUS.post(bridged);
        if (bridged.isCanceled()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event) {
        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(
                event.getGuiGraphics(),
                event.getPartialTick(),
                null
        ));
    }
}
