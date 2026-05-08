package top.ribs.scguns.compat.net.neoforged.neoforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;

public class RenderGuiOverlayEvent extends Event {
    private final GuiGraphics guiGraphics;
    private final float partialTick;
    private final Object overlay;
    private boolean canceled;

    public RenderGuiOverlayEvent() {
        this(new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource()),
                Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false),
                null);
    }

    public RenderGuiOverlayEvent(GuiGraphics guiGraphics, DeltaTracker partialTick, Object overlay) {
        this(guiGraphics, partialTick.getGameTimeDeltaPartialTick(false), overlay);
    }

    public RenderGuiOverlayEvent(GuiGraphics guiGraphics, float partialTick, Object overlay) {
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
        this.overlay = overlay;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public WindowProxy getWindow() {
        return new WindowProxy();
    }

    public Object getOverlay() {
        return overlay;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public static class Pre extends RenderGuiOverlayEvent {
        public Pre(GuiGraphics guiGraphics, DeltaTracker partialTick, Object overlay) {
            super(guiGraphics, partialTick, overlay);
        }
    }

    public static class Post extends RenderGuiOverlayEvent {
        public Post(GuiGraphics guiGraphics, DeltaTracker partialTick, Object overlay) {
            super(guiGraphics, partialTick, overlay);
        }
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
