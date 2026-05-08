package top.ribs.scguns.network.message;


import top.ribs.scguns.util.ItemStackNbtHelper;
import com.mrcrayfish.framework.api.network.LevelLocation;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.common.ReloadTracker;
import top.ribs.scguns.common.ReloadType;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.item.animated.AnimatedGunItem;
import top.ribs.scguns.network.PacketHandler;

public class C2SMessageGunLoaded extends PlayMessage<C2SMessageGunLoaded> {
    public C2SMessageGunLoaded() {}

    public void encode(C2SMessageGunLoaded message, FriendlyByteBuf buffer) {}

    public C2SMessageGunLoaded decode(FriendlyByteBuf buffer) {
        return new C2SMessageGunLoaded();
    }

    @Override
    public void handle(C2SMessageGunLoaded message, MessageContext context) {
        context.execute(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if (player != null && !player.isSpectator()) {
                ItemStack heldItem = player.getMainHandItem();
                if (heldItem.getItem() instanceof GunItem) {
                    if (!heldItem.getItem().getClass().getPackageName().startsWith("top.ribs.scguns")) {
                        return;
                    }
                    Gun gun = ((GunItem) heldItem.getItem()).getModifiedGun(heldItem);
                    ReloadTracker tracker = new ReloadTracker(player);
                    CompoundTag tag = tracker.getTag();

                    if (gun.getReloads().getReloadType() == ReloadType.MAG_FED) {
                        tracker.increaseMagAmmo(player);
                        stopReloading(player, tag);

                        syncReloadComplete(player, tag);

                    } else if (gun.getReloads().getReloadType() == ReloadType.SINGLE_ITEM) {
                        tracker.reloadItem(player);
                        stopReloading(player, tag);

                        syncReloadComplete(player, tag);

                    } else if (gun.getReloads().getReloadType() == ReloadType.MANUAL) {
                        tracker.increaseAmmo(player);

                        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateAmmo(tag.getInt("AmmoCount")));

                        boolean weaponFull = tracker.isWeaponFull(player);
                        boolean hasNoAmmo = tracker.hasNoAmmo(player);

                        if (weaponFull || hasNoAmmo) {
                            if (player.getMainHandItem().getItem() instanceof AnimatedGunItem) {
                                // Mark that we should stop after the current animation loop finishes
                                tag.putBoolean("scguns:ShouldStopAfterLoop", true);
                            } else {
                                // For non-animated guns, stop immediately
                                ModSyncedDataKeys.RELOADING.setValue(player, false);
                                tag.remove("IsReloading");
                                tag.remove("scguns:IsReloading");

                                PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageReload(false));
                            }
                        }
                    }
                }
            }
        });
        context.setHandled(true);
    }

    private static void syncReloadComplete(ServerPlayer player, CompoundTag tag) {
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageUpdateAmmo(tag.getInt("AmmoCount")));
        PacketHandler.getPlayChannel().sendToPlayer(() -> player, new S2CMessageReload(false));
    }

    private static void stopReloading(ServerPlayer player, CompoundTag tag) {
        ModSyncedDataKeys.RELOADING.setValue(player, false);
        tag.remove("IsReloading");
        tag.remove("scguns:IsReloading");
        tag.remove("InCriticalReloadPhase");
        tag.remove("scguns:ShouldStopAfterLoop");
        tag.remove("scguns:StopAfterLoopTime");
    }
}
