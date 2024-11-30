package org.land.skycraftclient.client;


import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import org.land.skycraftclient.client.animation.AnimationHandler;
import org.land.skycraftclient.client.animation.AnimationSetting;
import org.land.skycraftclient.client.animation.Animations;
import org.land.skycraftclient.client.packet.EnumMessageType;

public class AttackHandler {
    private int attackCombo = -1;
    private long lastAttackTime = 0;
    private static final long COMBO_RESET_TIME = 1000; // 1초


    public void register(){

        AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
            if (world.isClient() && entity instanceof LivingEntity) {
                if(MinecraftClient.getInstance().getCurrentServerEntry() == null) {
                    combo();
                    attackToMonster(playerEntity, Animations.getMessageType(playerEntity), attackCombo);
                }

                return ActionResult.PASS;
            }
            return ActionResult.PASS;
        });
        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            if(world.isClient()){
                ItemStack itemStack = playerEntity.getStackInHand(hand);
                if(itemStack.getItem() == Items.SHIELD){
                    AnimationHandler.playAnimation(playerEntity, hand == Hand.MAIN_HAND ? AnimationSetting.SHIELD_RIGHT : AnimationSetting.SHIELD_LEFT,0);

                }
                combo();
            }
            return TypedActionResult.pass(playerEntity.getStackInHand(hand));
        });
    }

    public void attackToMonster(PlayerEntity playerEntity, EnumMessageType enumMessageType, int combo){
        AnimationSetting.AnimationSet animationId = AnimationSetting.getAnimation(enumMessageType);
        AnimationHandler.playAnimation(playerEntity, animationId, combo);
    }


    public void combo(){
        long currentTime = System.currentTimeMillis();

        // 콤보 리셋 확인
        if (currentTime - lastAttackTime > COMBO_RESET_TIME) {
            attackCombo = -1;

        }
        attackCombo = (attackCombo + 1) % 3; // 3단 콤보 가정
        lastAttackTime = currentTime;

    }
    public boolean isMirror(ItemStack heldItem){
        EnumMessageType messageType = Animations.getMessageType(heldItem, new ItemStack(Items.AIR));
        if(messageType == EnumMessageType.SPEAR_ATTACK){
            return true;
        }
        if(messageType == EnumMessageType.NORMAL_SWORD_ATTACK){
            return false;
        }
        return false;
    }


}
