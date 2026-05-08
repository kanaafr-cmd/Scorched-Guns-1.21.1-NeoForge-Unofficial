package top.ribs.scguns.compat.net.neoforged.neoforge.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.LogicalSide;

public class TickEvent extends Event {
    public enum Phase {
        START,
        END
    }

    public final Phase phase;
    public final LogicalSide side;

    protected TickEvent(Phase phase, LogicalSide side) {
        this.phase = phase;
        this.side = side;
    }

    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(Phase phase) {
            super(phase, LogicalSide.CLIENT);
        }
    }

    public static class RenderTickEvent extends TickEvent {
        public final float renderTickTime;

        public RenderTickEvent(Phase phase, float renderTickTime) {
            super(phase, LogicalSide.CLIENT);
            this.renderTickTime = renderTickTime;
        }
    }

    public static class ServerTickEvent extends TickEvent {
        public ServerTickEvent(Phase phase) {
            super(phase, LogicalSide.SERVER);
        }
    }

    public static class PlayerTickEvent extends TickEvent {
        public final Player player;

        public PlayerTickEvent(Phase phase, LogicalSide side, Player player) {
            super(phase, side);
            this.player = player;
        }
    }
}
