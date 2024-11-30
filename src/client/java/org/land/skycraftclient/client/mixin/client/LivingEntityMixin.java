package org.land.skycraftclient.client.mixin.client;

import net.minecraft.entity.LivingEntity;
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
}
