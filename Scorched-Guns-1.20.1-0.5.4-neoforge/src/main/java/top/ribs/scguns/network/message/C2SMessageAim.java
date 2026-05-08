package top.ribs.scguns.network.message;


import top.ribs.scguns.util.ItemStackNbtHelper;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.GunItem;

public class C2SMessageAim extends PlayMessage<C2SMessageAim>
{
    private boolean aiming;

    public C2SMessageAim() {}

    public C2SMessageAim(boolean aiming)
    {
        this.aiming = aiming;
    }

    @Override
    public void encode(C2SMessageAim message, FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(message.aiming);
    }

    @Override
    public C2SMessageAim decode(FriendlyByteBuf buffer)
    {
        return new C2SMessageAim(buffer.readBoolean());
    }

    @Override
    public void handle(C2SMessageAim message, MessageContext context) {
        context.execute(() ->
        {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if(player != null && !player.isSpectator())
            {
                boolean currentlyReloading = ModSyncedDataKeys.RELOADING.getValue(player);

                if(currentlyReloading) {
                    ModSyncedDataKeys.AIMING.setValue(player, false);
                    return;
                }
                ItemStack heldItem = player.getMainHandItem();
                if(heldItem.getItem() instanceof GunItem) {
                    CompoundTag tag = ItemStackNbtHelper.getOrCreateTag(heldItem);
                    boolean inCriticalPhase = tag.getBoolean("InCriticalReloadPhase");

                    if(inCriticalPhase) {
                        ModSyncedDataKeys.AIMING.setValue(player, false);
                        return;
                    }
                }

                ModSyncedDataKeys.AIMING.setValue(player, message.aiming);
            }
        });
        context.setHandled(true);
    }
}