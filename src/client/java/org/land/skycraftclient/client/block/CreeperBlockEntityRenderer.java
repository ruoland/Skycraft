package org.land.skycraftclient.client.block;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.land.skycraftclient.block.CreeperBlockEntity;
public class CreeperBlockEntityRenderer implements BlockEntityRenderer<CreeperBlockEntity> {
    private static final Identifier CREEPER_TEXTURE = Identifier.of("textures/entity/creeper/creeper.png");
    private final EntityModel<Entity> creeperModel;

    public CreeperBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.creeperModel = new CreeperEntityModel<>(context.getLayerModelPart(EntityModelLayers.CREEPER));
    }


    @Override
    public void render(CreeperBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(-1F,-1F,1F);

        matrices.translate(0, -entity.getSize(),0);
        matrices.scale( entity.getSize(), entity.getSize(), entity.getSize());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getYaw()));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(CREEPER_TEXTURE));
        this.creeperModel.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();

    }
}

