package org.land.skycraftclient.client.animation;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.land.skycraftclient.client.ISkyAnimatedPlayer;

import java.util.*;

public class AnimationHandler {

    private static final HashMap<UUID, AnimationSetting.AnimationSet> PLAYER_ANIMATIONS = new HashMap<>();
    private static final HashMap<UUID, Queue<AnimationQueueEntry>> ANIMATION_QUEUES = new HashMap<>();
    private static boolean isTickEventRegistered = false;

    private static class AnimationQueueEntry {
        AnimationSetting.AnimationSet animationSet;
        int combo;
        int delay;

        AnimationQueueEntry(AnimationSetting.AnimationSet animationSet, int combo, int delay) {
            this.animationSet = animationSet;
            this.combo = combo;
            this.delay = delay;
        }
    }

    public static boolean isPlaying(PlayerEntity playerEntity) {
        ISkyAnimatedPlayer animatedPlayer = ((ISkyAnimatedPlayer) playerEntity);
        return animatedPlayer.skycraft_getModAnimation().getAnimation() != null && PLAYER_ANIMATIONS.containsKey(playerEntity.getUuid());
    }

    public static boolean isPlaying(PlayerEntity playerEntity, AnimationSetting.AnimationSet animationSetting) {
        return PLAYER_ANIMATIONS.get(playerEntity.getUuid()) == animationSetting;
    }

    public static void playAnimation(PlayerEntity playerEntity, AnimationSetting.AnimationSet animationSet, int combo) {
        UUID playerUuid = playerEntity.getUuid();
        ANIMATION_QUEUES.putIfAbsent(playerUuid, new LinkedList<>());
        Queue<AnimationQueueEntry> queue = ANIMATION_QUEUES.get(playerUuid);

        if (queue.isEmpty()) {
            // 큐가 비어있으면 즉시 재생
            System.out.println("즉시 재생");
            playAnimationImmediately(playerEntity, animationSet, combo);
        }

        registerTickEvent();
    }

    private static void playAnimationImmediately(PlayerEntity playerEntity, AnimationSetting.AnimationSet animationSet, int combo) {
        ISkyAnimatedPlayer animatedPlayer = ((ISkyAnimatedPlayer) playerEntity);
        ModifierLayer<IAnimation> container = animationSet.isMirror() ? animatedPlayer.skycraft_getMirrorAnimation() : animatedPlayer.skycraft_getModAnimation();
        KeyframeAnimation anim = (KeyframeAnimation) PlayerAnimationRegistry.getAnimation(animationSet.getIdentifier(combo));

        if(animationSet.getIdentifier(combo) == Animations.ANIMATION_STOP_SMOOTH)
        {
            System.out.println("대체됨 부드러움");
            container.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(animationSet.getEndTick(), (modelName, type, value) -> value), null);
            PLAYER_ANIMATIONS.remove(playerEntity.getUuid());
            return;
        }
        if (animationSet.getIdentifier(combo) == Animations.ANIMATION_STOP) {
            container.setAnimation(null);

            PLAYER_ANIMATIONS.remove(playerEntity.getUuid());
            System.out.println("중지됨");
            return;
        }

        if (anim == null) {
            playerEntity.sendMessage(Text.literal("애니메이션 재생 요청을 받아 불러오려 했지만, 불러오지 못하였습니다." + " - " + animationSet.getIdentifier(combo)));
            return;
        }

        var builder = anim.mutableCopy();
        builder.getPart("head").setEnabled(animationSet.isHead());
        builder.getPart("leftArm").setEnabled(animationSet.isLeftArm());
        builder.getPart("rightArm").setEnabled(animationSet.isRightArm());
        builder.getPart("leftLeg").setEnabled(animationSet.isLeftLeg());
        builder.getPart("rightLeg").setEnabled(animationSet.isRightLeg());
        builder.getPart("torso").setEnabled(animationSet.isBody());

        anim = builder.build();
        KeyframeAnimationPlayer kap = new KeyframeAnimationPlayer(anim, animationSet.getStartTick());

        kap.setFirstPersonConfiguration(animationSet.getFirstPersonConfiguration().setShowLeftItem(animationSet.isLeftItem()).setShowRightItem(animationSet.isRightItem())).setFirstPersonMode(animationSet.getPersonMode());

        if (container.getAnimation() != null || kap.getData().extraData.containsKey("isDodge")) {
            container.setAnimation(kap);
            PLAYER_ANIMATIONS.put(playerEntity.getUuid(), animationSet);
            System.out.println("애니메이션 설정됨");
            return;
        }

        if (animationSet.getSpeed() != 1) {
            container.addModifier(new SpeedModifier(animationSet.getSpeed()), 0);
        }

        kap = checkDodge(kap, combo, animationSet);

        container.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(animationSet.getEndTick(), (modelName, type, value) -> value), kap);
        System.out.println("자연스러운 재생");
        PLAYER_ANIMATIONS.put(playerEntity.getUuid(), animationSet);
    }

    private static KeyframeAnimationPlayer checkDodge(KeyframeAnimationPlayer kap, int combo, AnimationSetting.AnimationSet animationSet) {
        if (animationSet.getIdentifier(combo) == Animations.ANIMATION_DODGE)
            kap.getData().extraData.put("isDodge", true);
        return kap;
    }

    private static void registerTickEvent() {
        if (!isTickEventRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                for (UUID playerUuid : ANIMATION_QUEUES.keySet()) {
                    Queue<AnimationQueueEntry> queue = ANIMATION_QUEUES.get(playerUuid);
                    if (!queue.isEmpty()) {
                        AnimationQueueEntry entry = queue.peek();
                        if (entry.delay <= 0) {
                            queue.poll();
                            PlayerEntity player = client.world.getPlayerByUuid(playerUuid);
                            if (player != null) {
                                playAnimationImmediately(player, entry.animationSet, entry.combo);
                            }
                        } else {
                            entry.delay--;
                        }
                    }
                }
            });
            isTickEventRegistered = true;
        }
    }
}
