package top.ribs.scguns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import top.ribs.scguns.compat.net.neoforged.fml.DistExecutor;
import top.ribs.scguns.util.ItemStackNbtHelper;

public class S2CShowTotemAnimationMessage extends PlayMessage<S2CShowTotemAnimationMessage> {
    private ItemStack itemStack;

    public S2CShowTotemAnimationMessage() {}

    public S2CShowTotemAnimationMessage(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void encode(S2CShowTotemAnimationMessage message, FriendlyByteBuf buffer) {
        buffer.writeNbt(ItemStackNbtHelper.save(message.itemStack));
    }

    @Override
    public S2CShowTotemAnimationMessage decode(FriendlyByteBuf buffer) {
        S2CShowTotemAnimationMessage message = new S2CShowTotemAnimationMessage();
        CompoundTag tag = buffer.readNbt();
        message.itemStack = tag == null ? ItemStack.EMPTY : ItemStackNbtHelper.parse(tag);
        return message;
    }

    @Override
    public void handle(S2CShowTotemAnimationMessage message, MessageContext context) {
        context.execute(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.gameRenderer.displayItemActivation(message.itemStack);
            });
        });
        context.setHandled(true);
    }
}
