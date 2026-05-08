package top.ribs.scguns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.client.handler.ClientMeleeAttackHandler;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.util.ItemStackNbtHelper;

import java.util.function.Supplier;

public class S2CMessageMeleeAttack extends PlayMessage<S2CMessageMeleeAttack> {
    private ItemStack heldItem;

    public S2CMessageMeleeAttack() {}

    public S2CMessageMeleeAttack(ItemStack heldItem) {
        this.heldItem = heldItem;
    }

    public S2CMessageMeleeAttack(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        this.heldItem = tag == null ? ItemStack.EMPTY : ItemStackNbtHelper.parse(tag);
    }

    @Override
    public void encode(S2CMessageMeleeAttack message, FriendlyByteBuf buf) {
        buf.writeNbt(ItemStackNbtHelper.save(message.heldItem));
    }

    @Override
    public S2CMessageMeleeAttack decode(FriendlyByteBuf buf) {
        return new S2CMessageMeleeAttack(buf);
    }

    @Override
    public void handle(S2CMessageMeleeAttack message, MessageContext context) {
        context.execute(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                if (message.heldItem.getItem() instanceof GunItem gunItem) {
                    ClientMeleeAttackHandler.startMeleeAnimation(gunItem, message.heldItem);

                }
            }
        });
        context.setHandled(true);
    }
}
