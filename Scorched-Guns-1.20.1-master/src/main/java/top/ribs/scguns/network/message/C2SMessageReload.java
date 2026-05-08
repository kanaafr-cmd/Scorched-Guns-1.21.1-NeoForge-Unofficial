package top.ribs.scguns.network.message;

import top.ribs.scguns.util.ItemStackNbtHelper;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.client.handler.ReloadHandler;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ReloadType;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.GunItem;

/**
 * Author: MrCrayfish
 */
public class C2SMessageReload extends PlayMessage<C2SMessageReload> {
    private boolean reload;

    public C2SMessageReload() {}

    public C2SMessageReload(boolean reload) {
        this.reload = reload;
    }

    @Override
    public void encode(C2SMessageReload message, FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.reload);
    }

    @Override
    public C2SMessageReload decode(FriendlyByteBuf buffer) {
        return new C2SMessageReload(buffer.readBoolean());
    }

    @Override
    public void handle(C2SMessageReload message, MessageContext context) {
        context.execute(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if (player != null && !player.isSpectator()) {
                ItemStack heldItem = player.getMainHandItem();
                if (!(heldItem.getItem() instanceof GunItem) ||
                        !heldItem.getItem().getClass().getPackageName().startsWith("top.ribs.scguns")) {
                    return;
                }

                CompoundTag tag = ItemStackNbtHelper.getOrCreateTag(heldItem);
                Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem);
                boolean currentlyReloading = ModSyncedDataKeys.RELOADING.getValue(player);
                boolean currentlyAiming = ModSyncedDataKeys.AIMING.getValue(player);
                boolean inCriticalPhase = tag.getBoolean("InCriticalReloadPhase");

                if (message.reload) {
                    if (!currentlyReloading && !inCriticalPhase) {
                        ModSyncedDataKeys.RELOADING.setValue(player, true);
                        tag.putBoolean("IsReloading", true);
                        tag.putBoolean("scguns:IsReloading", true);
                        if (gun.getReloads().getReloadType() != ReloadType.MANUAL) {
                            tag.putBoolean("InCriticalReloadPhase", true);
                        }
                        tag.remove("scguns:ReloadState");
                    }
                } else {
                    if (gun.getReloads().getReloadType() != ReloadType.MANUAL && inCriticalPhase) {
                        return;
                    }
                    boolean wasReloading = tag.getBoolean("scguns:IsReloading");
                    boolean wasPlayingReloadStop = tag.getBoolean("scguns:IsPlayingReloadStop");
                    if (gun.getReloads().getReloadType() == ReloadType.MANUAL && wasReloading && !wasPlayingReloadStop) {
                        tag.putBoolean("scguns:IsPlayingReloadStop", true);
                        tag.putString("scguns:ReloadState", "STOPPING");
                        ReloadHandler.loaded(player);
                    }
                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                    tag.remove("IsReloading");
                    tag.remove("scguns:IsReloading");
                    tag.remove("InCriticalReloadPhase");
                    if (gun.getReloads().getReloadType() != ReloadType.MANUAL) {
                        tag.remove("scguns:ReloadState");
                    }
                }

                if (message.reload && currentlyAiming) {
                    ModSyncedDataKeys.AIMING.setValue(player, false);
                }
            }
        });
        context.setHandled(true);
    }
}
