package org.land.skycraftclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract int getFallFlyingTicks();

    @Shadow protected int fallFlyingTicks;

    @Shadow public abstract void playSound(@Nullable SoundEvent sound);

    @Inject(method = "isUsingRiptide", at = @At("HEAD"), cancellable = true)
    private void onIsUsingRiptide(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = ((LivingEntity)(Object)this);
        if (livingEntity.isFallFlying()) {
            if(getFallFlyingTicks() < 10) {
                fallFlyingTicks = 20;
            }
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        Entity thisEntity = (Entity)(Object)this;

        if (thisEntity instanceof PlayerEntity player) {

            if(player.isFallFlying()) {
                // 플레이어의 현재 속도 가져오기
                Vec3d currentVelocity = player.getVelocity();

                // 플레이어가 바라보는 방향 벡터 (앞으로 나아가는 방향)
                Vec3d forward = Vec3d.fromPolar(0, player.getYaw()).multiply(0.01); // 보정치 낮게 설정

                // 최대 속도 제한
                double maxSpeed = 1;

                // 현재 속도의 크기 계산
                double currentSpeed = currentVelocity.length();

                // 현재 속도가 최대 속도보다 낮을 경우에만 보정치 추가
                if (currentSpeed < maxSpeed) {
                    player.addVelocity(forward.x, forward.y, forward.z); // addVelocity로 보정치 추가
                }

                // 새로운 속도 계산 및 최대 속도 제한
                Vec3d newVelocity = player.getVelocity();
                double newSpeed = newVelocity.length();

                if (newSpeed > maxSpeed) {
                    // 최대 속도를 초과할 경우 부드럽게 조정
                    newVelocity = newVelocity.normalize().multiply(maxSpeed);
                    player.setVelocity(newVelocity);
                }

                // 서버에서 실행 중일 때만 패킷 전송
                if (!player.getWorld().isClient) {
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
                }
            }
            if(player.getVelocity().y < 0){
                CarryOnData carryOnData = CarryOnDataManager.getCarryData(player);
                if (carryOnData != null && carryOnData.isCarrying(CarryOnData.CarryType.ENTITY)) {
                    if (carryOnData.getEntity(player.getWorld()) instanceof ChickenEntity) {
                        // 현재 Y 속도 확인
                        double currentYVelocity = player.getVelocity().y;
                        // 최소 낙하 속도 설정 (예: -0.5)
                        double minFallSpeed = -0.5;
                        if (currentYVelocity < minFallSpeed) {
                            // 속도 조정
                            player.setVelocity(player.getVelocity().x, minFallSpeed, player.getVelocity().z);
                        }
                    }
                }

            }
        }
    }
}
