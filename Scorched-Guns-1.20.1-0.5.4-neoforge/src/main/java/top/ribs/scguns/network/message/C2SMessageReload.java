package top.ribs.scguns.network.message;


import top.ribs.scguns.util.ItemStackNbtHelper;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ReloadType;
import top.ribs.scguns.common.ReloadTracker;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.item.animated.AnimatedGunItem;
import top.ribs.scguns.network.PacketHandler;

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
                        if(currentlyAiming) {
                            ModSyncedDataKeys.AIMING.setValue(player, false);
                        }

                        ModSyncedDataKeys.RELOADING.setValue(player, true);
                        tag.putBoolean("IsReloading", true);
                        tag.putBoolean("scguns:IsReloading", true);
                        if (gun.getReloads().getReloadType() != ReloadType.MANUAL) {
                            tag.putBoolean("InCriticalReloadPhase", true);
                        }
                        tag.remove("scguns:ReloadState");

                        // Trigger animation for all players tracking the reloader
                        String animation = "reload";
                        if (heldItem.getItem() instanceof AnimatedGunItem animatedGun && animatedGun.isInCarbineMode(heldItem)) {
                            animation = "carbine_reload";
                        }
                        PacketHandler.getPlayChannel().sendToTrackingEntity(() -> player, new S2CMessageSyncReloadAnimation(player.getId(), animation));
                        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageSyncReloadAnimation(player.getId(), animation));
                    }
                } else {
                    boolean manualReloadStopping = gun.getReloads().getReloadType() == ReloadType.MANUAL &&
                            tag.getBoolean("scguns:IsReloading") &&
                            !tag.getBoolean("scguns:IsPlayingReloadStop");

                    if (!manualReloadStopping && gun.getReloads().getReloadType() != ReloadType.MANUAL && inCriticalPhase) {
                        return;
                    }

                    if (manualReloadStopping) {
                        tag.putBoolean("scguns:IsPlayingReloadStop", true);
                        tag.putString("scguns:ReloadState", "STOPPING");
                        ReloadTracker.loaded(player);

                        String animation = "reload_stop";
                        if (heldItem.getItem() instanceof AnimatedGunItem animatedGun && animatedGun.isInCarbineMode(heldItem)) {
                            animation = "carbine_reload_stop";
                        }
                        PacketHandler.getPlayChannel().sendToTrackingEntity(() -> player, new S2CMessageSyncReloadAnimation(player.getId(), animation));
                        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageSyncReloadAnimation(player.getId(), animation));
                    }

                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                    tag.remove("IsReloading");
                    tag.remove("scguns:IsReloading");
                    tag.remove("InCriticalReloadPhase");
                    if (!manualReloadStopping) {
                        tag.remove("scguns:ReloadState");
                        PacketHandler.getPlayChannel().sendToTrackingEntity(() -> player, new S2CMessageReload(false));
                        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageReload(false));
                    }
                }
            }
        });
        context.setHandled(true);
    }
}
