package top.ribs.scguns.compat.net.neoforged.neoforge.common.capabilities;

import net.minecraft.core.Direction;
import top.ribs.scguns.compat.net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface ICapabilityProvider {
    <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side);
}
