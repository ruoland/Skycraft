package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.land.skycraftclient.client.animation.AnimationHandler;
import org.land.skycraftclient.client.animation.AnimationSetting;
import org.land.skycraftclient.client.animation.Animations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClientEvent {
    public static double elytra_speed_config = 0.8f;
    public static double vertical_acceleration = 0.1; // 수직 가속도 설정
    public static double min_speed = 1.0; // 최소 속도 설정

    public void register(){



        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            ItemStack itemStack = playerEntity.getMainHandStack();
            //if(itemStack.getItem() == Items.STICK && world.isClient)
               // MinecraftClient.getInstance().setScreen(new ScreenCharacter(Text.literal("test")));
            return TypedActionResult.success(playerEntity.getStackInHand(hand));
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            PlayerEntity playerEntity = minecraftClient.player;
            boolean isPlayer = playerEntity != null;

            if(isPlayer) {
                boolean isFalling = playerEntity.fallDistance > 2 && !playerEntity.isOnGround() && !playerEntity.isFallFlying() && !playerEntity.isTouchingWater() && !playerEntity.hasStatusEffect(StatusEffects.LEVITATION);
                if(isFalling && !playerEntity.isFallFlying()) {
                    if (!AnimationHandler.isPlaying(playerEntity)) {
                        AnimationHandler.playAnimation(playerEntity, AnimationSetting.SKY_DIVING_FIRST, 0);
                    }
                }
                if(playerEntity.isFallFlying() && AnimationHandler.isPlaying(playerEntity) || (AnimationHandler.isPlaying(playerEntity, AnimationSetting.SKY_DIVING_FIRST) && !isFalling)){
                    AnimationHandler.playAnimation(playerEntity, AnimationSetting.NONE_SMOOTH, 0);
                }
            }
        });



    }
}
