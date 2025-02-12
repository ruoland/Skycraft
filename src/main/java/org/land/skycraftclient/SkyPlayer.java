package org.land.skycraftclient;


import net.minecraft.entity.player.PlayerEntity;
import org.land.skycraftclient.db.DBManager;
import org.land.skycraftclient.db.Skill;

import org.land.skycraftclient.skill.PlayerSkill;
import org.land.skycraftclient.skill.PlayerSkillManager;

import java.util.UUID;

public class SkyPlayer {
    private UUID uuid;

    private int level;
    private long experience;
    private int currentPartyId;
    private PlayerSkillManager playerSkillManager;
    private DBManager db;

    public SkyPlayer(String uuid, int level, long experience, int currentPartyId) {
        this.uuid = UUID.fromString(uuid);
        this.level = level;
        this.experience = experience;
        this.currentPartyId = currentPartyId;
        this.db = Skycraftclient.getDB();
        this.playerSkillManager = new PlayerSkillManager(this.uuid);
    }

    public void setPlayerSkills(PlayerSkillManager playerSkillManager) {
        this.playerSkillManager = playerSkillManager;
    }


    public void learnSkill(Skill newSkill) {
        if (!playerSkillManager.hasSkill(newSkill)) {
            PlayerSkill playerSkill = new PlayerSkill(newSkill);
            playerSkillManager.addSkill(newSkill);
            db.insertPlayerSkill(uuid, playerSkill);
        }
    }

    }
    public PlayerSkillManager getPlayerSkills() {
        return playerSkillManager;
    }

    public boolean hasSkill(Skill skill) {
        return playerSkillManager.hasSkill(skill);
    }

}
