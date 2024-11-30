package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.land.skycraftclient.client.animation.AnimationHandler;
import org.land.skycraftclient.client.animation.AnimationSetting;
import org.land.skycraftclient.client.animation.Animations;

import java.lang.reflect.Field;

public class ClientEvent {
    public void register(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            PlayerEntity playerEntity = minecraftClient.player;
            boolean isPlayer = playerEntity != null;

            if(isPlayer) {
                boolean isFalling = playerEntity.fallDistance > 1 && !playerEntity.isOnGround() && !playerEntity.isFallFlying() && !playerEntity.isTouchingWater() && !playerEntity.hasStatusEffect(StatusEffects.LEVITATION);
                if(isFalling && !playerEntity.isFallFlying()) {
                    if (!AnimationHandler.isPlaying(playerEntity)) {
                        AnimationHandler.playAnimation(playerEntity, AnimationSetting.SKY_DIVING_FIRST, 0);
                    }
                }
                if(playerEntity.isFallFlying() && AnimationHandler.isPlaying(playerEntity)){

                    AnimationHandler.playAnimation(playerEntity, AnimationSetting.NONE_SMOOTH, 0);
                }


            }

        });

    }

}
