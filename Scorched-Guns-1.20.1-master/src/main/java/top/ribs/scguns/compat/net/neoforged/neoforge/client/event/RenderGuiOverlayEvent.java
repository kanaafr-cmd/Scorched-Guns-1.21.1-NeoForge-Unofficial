package top.ribs.scguns.compat.net.neoforged.neoforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.bus.api.Event;

public class RenderGuiOverlayEvent extends Event {
    private boolean canceled;

    public float getPartialTick() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    public GuiGraphics getGuiGraphics() {
        return new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());
    }

    public WindowProxy getWindow() {
        return new WindowProxy();
    }

    public Object getOverlay() {
        return null;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public static class Pre extends RenderGuiOverlayEvent {
    }

    public static class Post extends RenderGuiOverlayEvent {
    }

    public static class WindowProxy {
        public int getGuiScaledWidth() {
            return Minecraft.getInstance().getWindow().getGuiScaledWidth();
        }

        public int getGuiScaledHeight() {
            return Minecraft.getInstance().getWindow().getGuiScaledHeight();
        }
    }
}
