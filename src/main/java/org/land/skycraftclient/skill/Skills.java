package org.land.skycraftclient.skill;



import java.util.TreeMap;

public class Skills {
    private static final TreeMap<Integer, Skill> SKILLS = new TreeMap<>();
    public static Skill ELYTRA_SPEED = add(new PassiveSkill().setName("겉날개").setDescription("겉날개를 마음껏 타고 날아다닐 수 있다. 레벨이 증가하면 겉날개의 속도가 증가하고, 내구도 감소 속도가 줄어든다.")
            .setId(1));
    public static Skill ELYTRA_BOOST = add(new BoostSkill().setName("바람 잠자리").setDescription("겉날개를 타고 다닐 때, Shift 키를 누르면 가속할 수 있다.")
            .setId(2).setDefaultCooldown(30000));
    public static Skill ELYTRA_FALL = add(new PassiveSkill().setName("가벼운 날개").setDescription("겉날개를 타고 있을 때 땅에 떨어져도 데미지가 감소한다.").setId(3));
    public static void init(){

    }
    private static Skill add(Skill skill){

        SKILLS.put(skill.getId(), skill);
        return skill;
    }

    public static Skill getById(int id){
        return SKILLS.get(id);
    }
    public static int getSkillSize(){
        return SKILLS.size();
    }
}
