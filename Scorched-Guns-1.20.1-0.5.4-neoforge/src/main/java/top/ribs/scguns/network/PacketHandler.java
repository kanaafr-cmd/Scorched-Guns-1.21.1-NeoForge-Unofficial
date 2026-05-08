package top.ribs.scguns.network;

import com.mrcrayfish.framework.api.FrameworkAPI;
import com.mrcrayfish.framework.api.network.FrameworkNetwork;
import com.mrcrayfish.framework.api.network.FrameworkNetworkBuilder;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import top.ribs.scguns.Reference;
import top.ribs.scguns.network.message.*;

import java.lang.reflect.InvocationTargetException;

public class PacketHandler
{
    private static FrameworkNetwork playChannel;

    public static void init()
    {
        FrameworkNetworkBuilder builder = FrameworkAPI.createNetworkBuilder(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "play"), 1);
        register(builder, C2SMessageSwapAmmo.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageJetpackState.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageJetpackThrust.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageOffhandMelee.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageSetBlueprintRecipe.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageToggleExoSuitPower.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageClearBlueprintRecipe.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageAim.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageUtilityAction.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageMeleeAttack.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageMuzzleFlash.class, PacketFlow.CLIENTBOUND);
        register(builder, C2SMessageReload.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageSaveExoSuitUpgrades.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageGunLoaded.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageEjectCasing.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageUpdateAmmo.class, PacketFlow.CLIENTBOUND);
        register(builder, C2SMessageShoot.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageChargeSync.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessagePreFireSound.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageUnload.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageStunGrenade.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageBulletTrail.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageTurretBulletTrail.class, PacketFlow.CLIENTBOUND);
        register(builder, C2SMessageAttachments.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageUpdateGuns.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageBlood.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageReload.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageBeamUpdate.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageBeamPenetration.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageStopBeam.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageBeamImpact.class, PacketFlow.CLIENTBOUND);
        register(builder, C2SMessageShooting.class, PacketFlow.SERVERBOUND);
        register(builder, C2SMessageStopBeam.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageGunSound.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageDualWieldShotCount.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageMeleeAttack.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageHotBarrelSync.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageStopReload.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageProjectileHitBlock.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageProjectileHitEntity.class, PacketFlow.CLIENTBOUND);
        register(builder, C2SMessageLeftOverAmmo.class, PacketFlow.SERVERBOUND);
        register(builder, S2CMessageRemoveProjectile.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CShowTotemAnimationMessage.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageSyncExoSuitUpgrades.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageEntityMuzzleFlash.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageEntityCasingEject.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageSyncUpgradeRegistry.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageRaidFlareBurst.class, PacketFlow.CLIENTBOUND);
        register(builder, S2CMessageSyncReloadAnimation.class, PacketFlow.CLIENTBOUND);
        playChannel = builder.build();

    }

    private static <T extends PlayMessage<T>> void register(FrameworkNetworkBuilder builder, Class<T> messageClass, PacketFlow flow)
    {
        T template = createTemplate(messageClass);
        StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.ofMember((message, buffer) -> message.encode(message, buffer), template::decode);
        builder.registerPlayMessage(messageClass.getSimpleName().toLowerCase(), messageClass, codec, (message, context) -> message.handle(message, context), flow);
    }

    private static <T extends PlayMessage<T>> T createTemplate(Class<T> messageClass)
    {
        try
        {
            return messageClass.getDeclaredConstructor().newInstance();
        }
        catch(NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalStateException("Unable to create network message template for " + messageClass.getName(), e);
        }
    }

    public static FrameworkNetwork getPlayChannel()
    {
        return playChannel;
    }


}
