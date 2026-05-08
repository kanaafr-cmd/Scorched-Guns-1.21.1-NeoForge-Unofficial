package top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities;

import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public final class ForgeCapabilities {
    public static final Capability<IItemHandler> ITEM_HANDLER = new Capability<>();
    public static final Capability<IEnergyStorage> ENERGY = new Capability<>();

    private ForgeCapabilities() {
    }
}
