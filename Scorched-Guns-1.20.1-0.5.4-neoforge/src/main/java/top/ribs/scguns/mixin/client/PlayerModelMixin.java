package top.ribs.scguns.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.client.handler.AimingHandler;
import top.ribs.scguns.client.render.gun.animated.AnimatedGunRenderer;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.item.WaraxeItem;
import top.ribs.scguns.item.animated.AnimatedGunItem;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Inject(method = "setupAnim*", at = @At(value = "TAIL"))
    private void setupAnimTail(T entity, float animationPos, float animationSpeed, float animationBob, float deltaHeadYaw, float headPitch, CallbackInfo ci) {
        if(!(entity instanceof Player player))
            return;

        PlayerModel<T> model = (PlayerModel<T>) (Object) this;
        ItemStack heldItem = player.getMainHandItem();

        if(heldItem.getItem() instanceof WaraxeItem waraxe) {
            float delta = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
            net.minecraft.client.model.HumanoidModel<LivingEntity> humanoidModel = (net.minecraft.client.model.HumanoidModel<LivingEntity>) (Object) model;
            waraxe.applyHoldingPose(humanoidModel, animationBob, player, heldItem, delta);
            copyModelAngles(model.rightArm, model.rightSleeve);
            copyModelAngles(model.leftArm, model.leftSleeve);
        }
        else if(heldItem.getItem() instanceof GunItem gunItem) {
            if (player.isSwimming() || player.isFallFlying() || player.isVisuallySwimming()) {
                applySwimmingGunPose(player, model, gunItem, heldItem);
                return;
            }
            Gun gun = gunItem.getModifiedGun(heldItem);
            gun.getGeneral().getGripType(heldItem).heldAnimation().applyPlayerModelRotation(player, model.rightArm, model.leftArm, model.head, InteractionHand.MAIN_HAND, AimingHandler.get().getAimProgress(player, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true)));

            copyModelAngles(model.rightArm, model.rightSleeve);
            copyModelAngles(model.leftArm, model.leftSleeve);
            copyModelAngles(model.head, model.hat);
        }
    }

    private void applySwimmingGunPose(Player player, PlayerModel<T> model, GunItem gunItem, ItemStack heldItem) {
        float aimProgress = 0.0f;
        if (player.isLocalPlayer()) {
            aimProgress = AimingHandler.get().getAimProgress(player, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true));
        }

        model.rightArm.xRot = (float) Math.toRadians(-160F + (aimProgress * 10F));
        model.rightArm.yRot = (float) Math.toRadians(-15F);
        model.rightArm.zRot = (float) Math.toRadians(10F);

        model.leftArm.xRot = (float) Math.toRadians(-140F);
        model.leftArm.yRot = (float) Math.toRadians(20F);
        model.leftArm.zRot = (float) Math.toRadians(-15F);

        copyModelAngles(model.rightArm, model.rightSleeve);
        copyModelAngles(model.leftArm, model.leftSleeve);
    }

    private static void copyModelAngles(ModelPart source, ModelPart target) {
        target.xRot = source.xRot;
        target.yRot = source.yRot;
        target.zRot = source.zRot;
    }
}
