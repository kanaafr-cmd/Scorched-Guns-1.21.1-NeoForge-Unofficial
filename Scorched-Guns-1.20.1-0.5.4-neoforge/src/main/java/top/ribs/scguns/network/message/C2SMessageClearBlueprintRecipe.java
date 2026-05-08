package top.ribs.scguns.network.message;


import top.ribs.scguns.util.ItemStackNbtHelper;
import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.item.BlueprintItem;

public class C2SMessageClearBlueprintRecipe extends PlayMessage<C2SMessageClearBlueprintRecipe> {
    private InteractionHand hand;

    public C2SMessageClearBlueprintRecipe() {}

    public C2SMessageClearBlueprintRecipe(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    public void encode(C2SMessageClearBlueprintRecipe message, FriendlyByteBuf buffer) {
        buffer.writeEnum(message.hand);
    }

    @Override
    public C2SMessageClearBlueprintRecipe decode(FriendlyByteBuf buffer) {
        return new C2SMessageClearBlueprintRecipe(buffer.readEnum(InteractionHand.class));
    }

    @Override
    public void handle(C2SMessageClearBlueprintRecipe message, MessageContext context) {
        context.execute(() -> {
            ServerPlayer player = (ServerPlayer) context.getPlayer().orElse(null);
            if (player != null) {
                ItemStack blueprint = player.getItemInHand(message.hand);

                if (blueprint.getItem() instanceof BlueprintItem) {
                    CompoundTag tag = ItemStackNbtHelper.getTag(blueprint);
                    if (tag != null && tag.contains("ActiveRecipe")) {
                        tag.remove("ActiveRecipe");

                        if (tag.isEmpty()) {
                            top.ribs.scguns.util.ItemStackNbtHelper.removeTag(blueprint);
                        } else {
                            ItemStackNbtHelper.setTag(blueprint, tag);
                        }
                    }
                }
            }
        });
        context.setHandled(true);
    }
}
