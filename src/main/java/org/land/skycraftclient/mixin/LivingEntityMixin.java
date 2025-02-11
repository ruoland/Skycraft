package org.land.skycraftclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract int getFallFlyingTicks();

    @Shadow protected int fallFlyingTicks;

    @Inject(method = "isUsingRiptide", at = @At("HEAD"), cancellable = true)
    private void onIsUsingRiptide(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = ((LivingEntity)(Object)this);
        if (livingEntity.isFallFlying()) {
            if(getFallFlyingTicks() < 10) {
                fallFlyingTicks = 20;
            }
        }
    }
    @Inject(method = "getPassengerRidingPos", at = @At("RETURN"), cancellable = true)
    private void onGetPassengerRidingPos(Entity passenger, CallbackInfoReturnable<Vec3d> cir) {
        Entity thisEntity = (Entity)(Object)this;

        if (thisEntity instanceof PlayerEntity player) {
            Vec3d originalPos = cir.getReturnValue();
            Vec3d forward = Vec3d.fromPolar(0, player.getYaw()).multiply(0.5 +passenger.getWidth()); // 앞으로 0.5블록
            Vec3d newPos = player.getPos().add(forward).add(0, 0.5, 0); // 플레이어 위치 + 앞으로 + 위로 1.5블록

            cir.setReturnValue(newPos);
        }
    }

}
