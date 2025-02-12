package org.land.skycraftclient.skill;


import net.minecraft.entity.player.PlayerEntity;
import org.land.skycraftclient.db.Skill;

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

    public boolean canUseSkill(String skillName) {
        PlayerSkill skill = skills.get(skillName);
        if (skill != null) {
            return skill.isReady();
        }
        return false;
    }

    public void useSkill(PlayerEntity player, Skill paramSkill) {
        PlayerSkill skill = skills.get(paramSkill.getName());
        if (skill != null && skill.isReady()) {
            skill.updateLastUsedTime();
            skill.use(player);
            skill.addExperience(10);


        }
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


}
