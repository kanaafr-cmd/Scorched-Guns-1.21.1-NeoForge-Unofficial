package top.ribs.scguns.network.message;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import top.ribs.scguns.client.network.ClientPlayHandler;

public class S2CMessageSyncReloadAnimation extends PlayMessage<S2CMessageSyncReloadAnimation> {
    private int entityId;
    private String animationName;

    public S2CMessageSyncReloadAnimation() {}

    public S2CMessageSyncReloadAnimation(int entityId, String animationName) {
        this.entityId = entityId;
        this.animationName = animationName;
    }

    @Override
    public void encode(S2CMessageSyncReloadAnimation message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityId);
        buffer.writeUtf(message.animationName);
    }

    @Override
    public S2CMessageSyncReloadAnimation decode(FriendlyByteBuf buffer) {
        return new S2CMessageSyncReloadAnimation(buffer.readInt(), buffer.readUtf());
    }

    @Override
    public void handle(S2CMessageSyncReloadAnimation message, MessageContext context) {
        context.execute(() -> {
            ClientPlayHandler.handleSyncReloadAnimation(message.entityId, message.animationName);
        });
        context.setHandled(true);
    }
}
