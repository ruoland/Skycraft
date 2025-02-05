package org.land.skycraftclient.component;


import org.ladysnake.cca.api.v3.component.Component;

import java.util.Map;

public interface SkillComponent extends Component {
    void addExperience(String skillName, int amount);
    int getSkillLevel(String skillName);
    boolean hasSkillLevel(String skillName, int level);
    void registerSkill(String skillName, int maxLevel);
    Map<String, Integer> getAllSkillLevels();
}