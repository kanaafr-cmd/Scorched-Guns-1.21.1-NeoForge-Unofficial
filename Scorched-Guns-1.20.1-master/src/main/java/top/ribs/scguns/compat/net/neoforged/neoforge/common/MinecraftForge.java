package top.ribs.scguns.compat.net.neoforged.neoforge.common;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public final class MinecraftForge {
    public static final IEventBus EVENT_BUS = NeoForge.EVENT_BUS;

    private MinecraftForge() {
    }
}
