package org.land.skycraftclient.client.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(LivingEntityRenderer.class)
public abstract class EntityRendererMixin<T extends LivingEntity> {

    @Shadow protected abstract void scale(T entity, MatrixStack matrices, float amount);

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
                          VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        float scale = getRandomScale(entity);
        matrices.push();
        matrices.scale(scale, scale, scale);
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void afterRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
                             VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        matrices.pop();
    }

    private float getRandomScale(Entity entity) {
        // 엔티티 유형에 따라 다른 크기 범위 설정
        float minScale = 0.8f;
        float maxScale = 1.2f;
        
        // 엔티티의 고유 ID를 시드로 사용하여 일관된 크기 생성
        Random random = new Random(entity.getUuid().getLeastSignificantBits());
        return minScale + random.nextFloat() * (maxScale - minScale);
    }
}
