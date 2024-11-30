package org.land.skycraftclient.client.animation;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.client.packet.EnumMessageType;

import java.util.Arrays;

public class AnimationSetting {
    public static final AnimationSet NORMAL_SWING = new AnimationSet().setComboIdentifier(Animations.ANIMATION_NORMAL_SWORD).setCombo();
    public static final AnimationSet BOW = new AnimationSet().setIdentifier(Animations.ANIMATION_BOW).setPersonMode(FirstPersonMode.VANILLA).setLeftArm(false).setLeftItem(false);
    public static final AnimationSet DODGE = new AnimationSet().setIdentifier(Animations.ANIMATION_DODGE).setFirstPersonDisable();
    public static final AnimationSet SHIELD_LEFT = new AnimationSet().setIdentifier(Animations.ANIMATION_SHIELD_LEFT).setLeftLeg(false).setRightLeg(false).setStartTick(5).setLeftItem(true);
    public static final AnimationSet SHIELD_RIGHT = new AnimationSet().setIdentifier(Animations.ANIMATION_SHIELD_RIGHT).setLeftLeg(false).setRightLeg(false).setStartTick(5);
    public static final AnimationSet SPEAR_ATTACK = new AnimationSet().setComboIdentifier(Animations.ANIMATION_SPEAR).setCombo().setHead(true).setLeftItem(false);
    public static final AnimationSet NONE = new AnimationSet().setFirstPersonDisable();
    public static final AnimationSet NONE_SMOOTH = new AnimationSet().setFirstPersonDisable().setIdentifier(Animations.ANIMATION_STOP_SMOOTH);
    public static final AnimationSet DOUBLE_SWORD = new AnimationSet().setComboIdentifier(Animations.ANIMATION_DOUBLE_SWORD).setCombo();
    public static final AnimationSet DOUBLE_EDGED = new AnimationSet().setComboIdentifier(Animations.ANIMATION_DOUBLE_EDGED).setCombo();
    public static final AnimationSet TWO_HANDED_SWORD = new AnimationSet().setComboIdentifier(Animations.ANIMATION_TWO_HANDED_SWORD).setLeftItem(false);
    public static final AnimationSet WAND = new AnimationSet().setIdentifier(Animations.ANIMATION_WAND);
    public static final AnimationSet JUMP = new AnimationSet().setIdentifier(Animations.ANIMATION_JUMP).setFirstPersonDisable().setStartTick(0);
    public static final AnimationSet JUMP_LEFT = new AnimationSet().setIdentifier(Animations.ANIMATION_JUMP_LEFT).setFirstPersonDisable().setStartTick(0);
    public static final AnimationSet JUMP_RIGHT = new AnimationSet().setIdentifier(Animations.ANIMATION_JUMP_RIGHT).setFirstPersonDisable().setStartTick(0);
    public static final AnimationSet JUMP_RUN_LEFT = new AnimationSet().setIdentifier(Animations.ANIMATION_JUMP_RUN_LEFT).setFirstPersonDisable().setStartTick(0);
    public static final AnimationSet JUMP_RUN_RIGHT = new AnimationSet().setIdentifier(Animations.ANIMATION_JUMP_RUN_RIGHT).setFirstPersonDisable().setStartTick(0);
    public static final AnimationSet WALK = new AnimationSet().setIdentifier(Animations.ANIMATION_WALK).setFirstPersonDisable().setStartTick(0).setEndTick(15);
    public static final AnimationSet SKY_DIVING_FIRST = new AnimationSet().setIdentifier(Animations.ANIMATION_SKY_DIVING).setFirstPersonDisable().setStartTick(0).setEndTick(20);


    public static AnimationSet getAnimation(EnumMessageType message){
        if(message == null)
            return AnimationSetting.NONE;
        return switch (message) {
            case DODGE -> DODGE;
            case NORMAL_SWORD_ATTACK -> NORMAL_SWING;
            case DOUBLE_SWORD_ATTACK -> DOUBLE_SWORD;
            case SPEAR_ATTACK -> SPEAR_ATTACK;

            case SHIELD_STOP -> NONE;
            case DODGE_FAILED -> null;
            case DODGE_SUCCESS -> DODGE;
            case DOUBLE_EDGED_ATTACK ->DOUBLE_EDGED;
            case BOW -> BOW;
            case BOW_SHOOT -> BOW; //TODO
            case TWO_HANDED_SWORD -> TWO_HANDED_SWORD;
            case WAND -> WAND;
            case SHIELD_LEFT -> SHIELD_LEFT;
            case SHIELD_RIGHT -> SHIELD_RIGHT;
            case JUMP -> JUMP;
            case JUMP_WALK_LEFT -> JUMP_LEFT;
            case JUMP_WALK_RIGHT -> JUMP_RIGHT;
            case WALK -> WALK;
            case JUMP_RUN_LEFT -> JUMP_RUN_LEFT;
            case JUMP_RUN_RIGHT -> JUMP_RUN_RIGHT;
        };
    }

    public static class AnimationSet{
        private boolean isMirror =  false;
        private boolean isHead = true, isBody = true, isLeftArm= true, isRightArm = true, isLeftLeg = true, isRightLeg = true;
        private boolean isLeftItem = false, isRightItem = true;
        private boolean isCombo;
        private int combo;
        private Identifier identifier = Animations.ANIMATION_STOP;
        private Identifier[] comboIdentifier;
        private int startTick = 5, endTick = 0;
        private float speed = 1;
        private FirstPersonConfiguration firstPersonConfiguration = new FirstPersonConfiguration().setShowLeftArm(true).setShowRightArm(true);
        private FirstPersonMode personMode = FirstPersonMode.THIRD_PERSON_MODEL;

        public AnimationSet setFirstPersonConfiguration(FirstPersonConfiguration firstPersonConfiguration) {
            this.firstPersonConfiguration = firstPersonConfiguration;
            return this;
        }

        public float getSpeed() {

            return speed;
        }

        public AnimationSet setEndTick(int endTick) {
            this.endTick = endTick;
            return this;
        }

        public AnimationSet setStartTick(int startTick) {
            this.startTick = startTick;
            return this;
        }

        public AnimationSet setCombo(int combo) {
            this.combo = combo;
            return this;
        }

        public AnimationSet setCombo() {
            isCombo = true;
            return this;
        }

        public AnimationSet setMirror() {
            isMirror = true;
            return this;
        }

        public AnimationSet setComboIdentifier(Identifier[] comboIdentifier) {
            this.comboIdentifier = comboIdentifier;


            return setCombo();
        }

        public AnimationSet setMirror(boolean condition){
            isMirror = condition;
            return this;
        }

        public AnimationSet setHead(boolean head) {
            isHead = head;
            return this;
        }

        public AnimationSet setBody(boolean body) {
            isBody = body;
            return this;
        }

        public AnimationSet setLeftArm(boolean leftArm) {
            isLeftArm = leftArm;
            return this;
        }

        public AnimationSet setRightArm(boolean rightArm) {
            isRightArm = rightArm;
            return this;
        }

        public AnimationSet setLeftLeg(boolean leftLeg) {
            isLeftLeg = leftLeg;
            return this;
        }

        public AnimationSet setRightLeg(boolean rightLeg) {
            isRightLeg = rightLeg;
            return this;
        }

        public AnimationSet setFirstPersonDisable(){
            personMode = FirstPersonMode.NONE;
            return setFirstPersonConfiguration(new FirstPersonConfiguration());
        }

        public AnimationSet setPersonMode(FirstPersonMode personMode) {
            this.personMode = personMode;
            return this;
        }

        public boolean isLeftItem() {
            return isLeftItem;
        }

        public AnimationSet setLeftItem(boolean leftItem) {
            isLeftItem = leftItem;
            return this;
        }

        public boolean isRightItem() {
            return isRightItem;
        }

        public AnimationSet setRightItem(boolean rightItem) {
            isRightItem = rightItem;
            return this;
        }

        public FirstPersonMode getPersonMode() {
            return personMode;
        }

        ;
        public AnimationSet setIdentifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }

        public boolean isMirror() {
            return isMirror;
        }

        public boolean isHead() {
            return isHead;
        }

        public boolean isBody() {
            return isBody;
        }

        public boolean isLeftArm() {
            return isLeftArm;
        }

        public boolean isRightArm() {
            return isRightArm;
        }

        public boolean isLeftLeg() {
            return isLeftLeg;
        }

        public boolean isRightLeg() {
            return isRightLeg;
        }

        public boolean isCombo() {
            return isCombo;
        }

        public int getCombo() {
            return combo;
        }

        public Identifier getIdentifier(int combo) {
            if(comboIdentifier != null && comboIdentifier.length > 0)
                return comboIdentifier[combo];
            else
                return identifier;
        }

        public int getStartTick() {
            return startTick;
        }

        public int getEndTick() {
            return endTick;
        }


        public FirstPersonConfiguration getFirstPersonConfiguration() {
            return firstPersonConfiguration;
        }

        @Override
        public String toString() {
            return "AnimationSet{" +
                    "isMirror=" + isMirror +
                    ", isHead=" + isHead +
                    ", isBody=" + isBody +
                    ", isLeftArm=" + isLeftArm +
                    ", isRightArm=" + isRightArm +
                    ", isLeftLeg=" + isLeftLeg +
                    ", isRightLeg=" + isRightLeg +
                    ", isCombo=" + isCombo +
                    ", combo=" + combo +
                    ", identifier=" + identifier +
                    ", comboIdentifier=" + Arrays.toString(comboIdentifier) +
                    ", startTick=" + startTick +
                    ", endTick=" + endTick +
                    ", firstPersonConfiguration=" + firstPersonConfiguration +
                    '}';
        }
    }
}
