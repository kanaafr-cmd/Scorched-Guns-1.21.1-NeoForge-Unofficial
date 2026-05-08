package top.ribs.scguns.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import top.ribs.scguns.Reference;
import top.ribs.scguns.entity.monster.SulfurheadEntity;

public class SulfurheadGelLayer extends RenderLayer<SulfurheadEntity, SulfurheadModel<SulfurheadEntity>> {
    private static final ResourceLocation GEL_TEXTURE = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/entity/sulfurhead_gel.png");

    public SulfurheadGelLayer(RenderLayerParent<SulfurheadEntity, SulfurheadModel<SulfurheadEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       SulfurheadEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(GEL_TEXTURE));

        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 0x66FFFF4D);
    }
}
