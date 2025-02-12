package org.land.skycraftclient.skill;


import java.util.*;

public class PlayerSkillManager {
    private UUID playerUUID;
    private Map<String, PlayerSkill> skills;

    public PlayerSkillManager(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.skills = new HashMap<>();
    }

    public void addSkill(Skill skill) {
        skills.put(skill.getName(), new PlayerSkill(skill));
    }

    public void useSkill(Skill skillName, int exp) {
        PlayerSkill skill = skills.get(skillName.getName());
        if (skill != null) {
            skill.use();
            skill.addExperience(exp);
            skill.setCooldown(skill.getCooldown());
        }
    }

    public Map<String, PlayerSkill> getSkills() {
        return skills;
    }

    public void updateSkillLevel(String skillName, int newLevel) {
        PlayerSkill skill = skills.get(skillName);
        if (skill != null) {
            skill.setLevel(newLevel);
        }
    }

    public void updateSkillCooldown(String skillName, long newCooldown) {
        PlayerSkill skill = skills.get(skillName);
        if (skill != null) {
            skill.setCooldown(newCooldown);
        }
    }
    public void useSkill(Skill skill){
        getSkill(skill).use();
    }

    public boolean hasSkill(Skill skillName) {
        return skills.containsKey(skillName.getName());
    }

    public PlayerSkill getSkill(Skill skillName) {
        return skills.get(skillName.getName());
    }

    public List<PlayerSkill> getAllSkills() {
        return new ArrayList<>(skills.values());
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
