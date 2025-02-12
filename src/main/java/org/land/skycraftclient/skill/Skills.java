package org.land.skycraftclient.skill;



import org.land.skycraftclient.db.Skill;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
public class Skills {
    private static final Map<String, Skill> SKILLS = new HashMap<>();

    public static final Skill ELYTRA_SPEED = add(new PassiveSkill()
            .setLocalizationName("겉날개")
            .setDescription("겉날개의 속도가 증가하고 내구도 감소 속도가 줄어든다.")
            .setId(1));

    public static final Skill ELYTRA_BOOST = add(new BoostSkill()
            .setLocalizationName("바람 잠자리")
            .setDescription("Shift 키로 가속 가능.")
            .setId(2)
            .setDefaultCooldown(500));

    public static final Skill ELYTRA_FALL = add( new PassiveSkill()
            .setLocalizationName("가벼운 날개")
            .setDescription("땅에 떨어져도 데미지가 감소한다.")
            .setId(3));

    private static Skill add(Skill skill) {
        SKILLS.put(skill.getName(), skill);
        return skill;
    }

    public static Skill getByType(String type) {
        return SKILLS.get(type);
    }
}
