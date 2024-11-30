package org.land.skycraftclient.client.animation;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.client.packet.EnumMessageType;

public record Animations() {
    public static final Identifier ANIMATION_STOP = Identifier.of("skycraftclient", "none");
    public static final Identifier ANIMATION_STOP_SMOOTH = Identifier.of("skycraftclient", "none_smooth");
    public static final Identifier[] ANIMATION_DOUBLE_SWORD = {Identifier.of("skycraftclient", "animation_normal_double_sword_1")
            , Identifier.of("skycraftclient", "animation_normal_double_sword_2")
            , Identifier.of("skycraftclient", "animation_cross_swing")
    , };
    public static final Identifier[] ANIMATION_DOUBLE_EDGED = {Identifier.of("skycraftclient", "animation_double_edged_1")
            , Identifier.of("skycraftclient", "animation_double_edged_2")
            , Identifier.of("skycraftclient", "animation_double_edged_3")
            , };
    public static final Identifier[] ANIMATION_SPEAR = {Identifier.of("skycraftclient", "animation_spear1_1")
    , Identifier.of("skycraftclient", "animation_spear1_2_right"), Identifier.of("skycraftclient", "animation_spear1_3_right")};

    public static final Identifier ANIMATION_NORMAL_SWORD[] = {Identifier.of("skycraftclient", "animation_normal_sword_attack1")
            , Identifier.of("skycraftclient", "animation_normal_sword_attack2"), Identifier.of("skycraftclient", "animation_normal_sword_attack3")};

    public static final Identifier ANIMATION_DODGE = Identifier.of("skycraftclient", "animation_dodge");
    public static final Identifier ANIMATION_SHIELD_RIGHT = Identifier.of("skycraftclient", "animation_shield_right");
    public static final Identifier ANIMATION_SHIELD_LEFT = Identifier.of("skycraftclient", "animation_shield_left");
    public static final Identifier ANIMATION_BOW = Identifier.of("skycraftclient", "animation_bow");
    public static final Identifier ANIMATION_BOW_SHOOT = Identifier.of("skycraftclient", "animation_bow_shoot");
    public static final Identifier[] ANIMATION_TWO_HANDED_SWORD = {Identifier.of("skycraftclient", "animation_big_sword_1"), Identifier.of("skycraftclient", "animation_big_sword_2"), Identifier.of("skycraftclient", "animation_big_sword_3")};
    public static final Identifier ANIMATION_WAND = Identifier.of("skycraftclient", "animation_wand");
    public static final Identifier ANIMATION_JUMP = Identifier.of("skycraftclient", "animation_jump");
    public static final Identifier ANIMATION_JUMP_LEFT = Identifier.of("skycraftclient", "animation_jump_left");
    public static final Identifier ANIMATION_JUMP_RIGHT = Identifier.of("skycraftclient", "animation_jump_right");
    public static final Identifier ANIMATION_WALK = Identifier.of("skycraftclient", "animation_walk");
    public static final Identifier ANIMATION_JUMP_RUN_LEFT = Identifier.of("skycraftclient", "animation_jump_run_left");
    public static final Identifier ANIMATION_JUMP_RUN_RIGHT = Identifier.of("skycraftclient", "animation_jump_run_right");
    public static final Identifier ANIMATION_SKY_DIVING = Identifier.of("skycraftclient", "animation_sky_fall");

    public static EnumMessageType getMessageType(PlayerEntity playerEntity){

        return getMessageType(playerEntity.getMainHandStack(), playerEntity.getOffHandStack());
    }
    public static AnimationSetting.AnimationSet getAnimation(String value){
        EnumMessageType messageType = EnumMessageType.valueOf(value);
        return AnimationSetting.getAnimation(messageType);
    }

    public static EnumMessageType getMessageType(ItemStack itemStack, ItemStack heldItem){
        Item item = itemStack.getItem();
        if(item == Items.STICK){
            return EnumMessageType.DOUBLE_EDGED_ATTACK;
        }
        if(item instanceof TridentItem){
            return EnumMessageType.SPEAR_ATTACK;
        }
        if(item instanceof SwordItem) {
            if (heldItem != null && heldItem.getItem() instanceof SwordItem)
                return EnumMessageType.DOUBLE_SWORD_ATTACK;
            else
                return EnumMessageType.NORMAL_SWORD_ATTACK;
        }
        if(item instanceof ShieldItem){
            return EnumMessageType.SHIELD_RIGHT;
        }
        if(heldItem.getItem() instanceof ShieldItem){
            return EnumMessageType.SHIELD_LEFT;
        }
        if(item instanceof AxeItem){
            return EnumMessageType.TWO_HANDED_SWORD;
        }

        return null;
    }
}
