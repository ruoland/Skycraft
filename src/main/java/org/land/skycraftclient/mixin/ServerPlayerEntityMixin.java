package org.land.skycraftclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.land.skycraftclient.SkyPlayer;
import org.land.skycraftclient.Skycraftclient;
import org.land.skycraftclient.skill.Skills;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Shadow public abstract ServerWorld getServerWorld();


    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.hasPassengers()) {
            Entity passenger = player.getFirstPassenger();
            if (!passenger.isAlive()) {
                passenger.stopRiding();
            }
        }
    }
    @Inject(method = "handleFall", at = @At("HEAD"), cancellable = true)
    private void onHandleFall(double xDifference, double yDifference, double zDifference, boolean onGround, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        SkyPlayer skyPlayer = Skycraftclient.getPlayer(player.getUuid());

        if (player.isFallFlying()) {
            if (!skyPlayer.hasSkill(Skills.ELYTRA_FALL)) {
                skyPlayer.learnSkill(Skills.ELYTRA_FALL);
                player.sendMessage(Text.of("겉날개를 착용한 채로 추락하여 " + Skills.ELYTRA_FALL.getName() + " 를 배웠습니다!"));
                player.sendMessage(Text.of("이제부터 추락해도 데미지가 상당히 경감되어 적용됩니다."));
            }

            if (skyPlayer.hasSkill(Skills.ELYTRA_FALL)) {
                // 낙하 거리 계산 (yDifference가 음수일 때만 고려)
                float fallDistance = (float) Math.max(0, -yDifference);

                float damageMultiplier = 1.0F;  // 기본 데미지 배수
                float reducedDamage = fallDistance * 0.1F * damageMultiplier;

                if (!player.getWorld().isClient) {
                    // 서버 사이드에서만 데미지 적용 및 효과 부여
                    player.damage(new DamageSource(getServerWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.FALL)), reducedDamage);
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 0, false, false));


                    // 원래의 낙하 처리를 취소
                    ci.cancel();
                }
            }
        }
    }



    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        if (player.hasPassengers()) {
            player.removeAllPassengers();
        }
    }


}
