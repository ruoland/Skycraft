package org.land.skycraftclient.skill;

import net.minecraft.entity.player.PlayerEntity;
import org.land.skycraftclient.Skycraftclient;
import org.land.skycraftclient.db.Skill;

import java.time.Duration;
import java.time.Instant;

public class PlayerSkill {
        private Skill skill;

        private int level;
        private int experience;
        private Instant lastUsedTime;

        public PlayerSkill(Skill skill) {
            this.skill = skill;
            this.level = 1;
            this.experience = 0;
            lastUsedTime = Instant.EPOCH;
        }
        public void use(PlayerEntity player){

            skill.onUse(player);
        }
        public boolean isReady() {
            Duration cooldown = Duration.ofMillis(skill.getDefaultCooldown());

            return true || Duration.between(lastUsedTime, Instant.now()).compareTo(cooldown) >= 0;
        }

        public void updateLastUsedTime() {
            this.lastUsedTime = Instant.now();
        }

        public Duration getRemainingCooldown() {
            Duration cooldown = Duration.ofMillis(skill.getDefaultCooldown());
            Duration elapsed = Duration.between(lastUsedTime, Instant.now());
            return cooldown.minus(elapsed).isNegative() ? Duration.ZERO : cooldown.minus(elapsed);
        }
        public void addExperience(int exp) {
            this.experience += exp;
            checkLevelUp();
        }

        private void checkLevelUp() {
            int requiredExp = level * 100; // 레벨당 100 경험치 필요
            while (experience >= requiredExp) {
                experience -= requiredExp;
                level++;
                requiredExp = level * 100; // 다음 레벨업에 필요한 경험치 갱신
            }
        }

        public org.land.skycraftclient.skill.PlayerSkill setLevel(int level) {
            this.level = level;
            return this;
        }

        public org.land.skycraftclient.skill.PlayerSkill setExperience(int experience) {
            this.experience = experience;
            return this;
        }

        public Skill getSkill() {
            return skill;
        }

        public int getLevel() {
            return level;
        }

        public int getExperience() {
            return experience;
        }


        @Override
        public String toString() {
            return String.format("PlayerSkill{skill=%s, level=%d, experience=%d, cooldown=%d, lastUsed=%d}",
                    skill.getName(), level, experience);
        }
    }

