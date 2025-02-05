package org.land.skycraftclient.component;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import java.util.HashMap;
import java.util.Map;

public class SkillComponentImpl implements SkillComponent {
    private final Map<String, SkillData> skills = new HashMap<>();
    private final PlayerEntity player;

    public SkillComponentImpl(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public void addExperience(String skillName, int amount) {
        SkillData data = skills.get(skillName);
        if (data != null) {
            int oldLevel = data.level;
            data.experience += amount;
            while (data.experience >= getExperienceForNextLevel(data.level) && data.level < data.maxLevel) {
                data.level++;
                data.experience -= getExperienceForNextLevel(data.level - 1);
                onLevelUp(skillName, data.level);
            }
            if (data.level != oldLevel) {
                SKILL_LEVEL_UP.invoker().onSkillLevelUp(player, skillName, data.level);
            }
        }
    }
    @Override
    public int getSkillLevel(String skillName) {
        SkillData data = skills.get(skillName);
        return data != null ? data.level : 0;
    }

    @Override
    public boolean hasSkillLevel(String skillName, int level) {
        return getSkillLevel(skillName) >= level;
    }

    @Override
    public void registerSkill(String skillName, int maxLevel) {
        skills.putIfAbsent(skillName, new SkillData(maxLevel));
    }

    @Override
    public Map<String, Integer> getAllSkillLevels() {
        Map<String, Integer> levels = new HashMap<>();
        for (Map.Entry<String, SkillData> entry : skills.entrySet()) {
            levels.put(entry.getKey(), entry.getValue().level);
        }
        return levels;
    }

    private int getExperienceForNextLevel(int currentLevel) {
        return 100 * (currentLevel + 1);
    }

    private void onLevelUp(String skillName, int newLevel) {
        // 레벨업 시 추가 로직 구현
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound skillsTag = nbtCompound.getCompound("Skills");
        for (String key : skillsTag.getKeys()) {
            NbtCompound skillTag = skillsTag.getCompound(key);
            SkillData data = new SkillData(skillTag.getInt("MaxLevel"));
            data.level = skillTag.getInt("Level");
            data.experience = skillTag.getInt("Experience");
            skills.put(key, data);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup wrapperLookup) {
        NbtCompound skillsTag = new NbtCompound();
        for (Map.Entry<String, SkillData> entry : skills.entrySet()) {
            NbtCompound skillTag = new NbtCompound();
            skillTag.putInt("Level", entry.getValue().level);
            skillTag.putInt("Experience", entry.getValue().experience);
            skillTag.putInt("MaxLevel", entry.getValue().maxLevel);
            skillsTag.put(entry.getKey(), skillTag);
        }
        nbtCompound.put("Skills", skillsTag);
    }

    private static class SkillData {
        int level;
        int experience;
        final int maxLevel;

        SkillData(int maxLevel) {
            this.level = 0;
            this.experience = 0;
            this.maxLevel = maxLevel;
        }
    }


    public static final Event<SkillLevelUp> SKILL_LEVEL_UP = EventFactory.createArrayBacked(SkillLevelUp.class,
            (listeners) -> (player, skillName, newLevel) -> {
                for (SkillLevelUp listener : listeners) {
                    listener.onSkillLevelUp(player, skillName, newLevel);
                }
            });

    public interface SkillLevelUp {
        void onSkillLevelUp(PlayerEntity player, String skillName, int newLevel);
    }
}
