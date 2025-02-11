package org.land.skycraftclient.skill;


import org.land.skycraftclient.db.Skill;

public class PlayerSkill {
    private Skill skill;
    private int level;
    private int experience;
    private long cooldown;
    private long lastUsed;

    public PlayerSkill(Skill skill) {
        this.skill = skill;
        this.level = 1;
        this.experience = 0;
        this.cooldown = skill.getDefaultCooldown();
        this.lastUsed = 0;
    }

    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
    }

    public boolean canUseSkill() {
        return System.currentTimeMillis() - lastUsed >= cooldown;
    }

    public void use() {
        if (canUseSkill()) {
            lastUsed = System.currentTimeMillis();
        }
    }

    // Getter 메서드들
    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public long getCooldown() {
        return cooldown;
    }

    public long getLastUsed() {
        return lastUsed;
    }

    // Setter 메서드들 (메소드 체이닝을 위해 PlayerSkill을 반환)
    public PlayerSkill setSkill(Skill skill) {
        this.skill = skill;
        return this;
    }

    public PlayerSkill setLevel(int level) {
        this.level = level;
        return this;
    }

    public PlayerSkill setExperience(int experience) {
        this.experience = experience;
        return this;
    }

    public PlayerSkill setCooldown(long cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    public PlayerSkill setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
        return this;
    }

    @Override
    public String toString() {
        return String.format("PlayerSkill{skill=%s, level=%d, experience=%d, cooldown=%d, lastUsed=%d}",
                skill.getName(), level, experience, cooldown, lastUsed);
    }
}