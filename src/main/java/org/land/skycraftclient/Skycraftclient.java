package org.land.skycraftclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.land.skycraftclient.component.SkillComponent;
import org.land.skycraftclient.component.SkillComponentImpl;
import org.land.skycraftclient.register.ModBlockEntities;
import org.land.skycraftclient.register.SkyBlockRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Skycraftclient implements ModInitializer, EntityComponentInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Skycraftclient.class);
    public static final ComponentKey<SkillComponent> SKILLS =
            ComponentRegistry.getOrCreate(Identifier.of("skycraftclient", "skills"), SkillComponent.class);

    @Override
    public void onInitialize() {
        ElytraEvent elytraEvent = new ElytraEvent();
        elytraEvent.register();
        SkyBlockRegister.initialize();


        // 아이템 그룹에 캠프 관련 아이템 추가
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(new ItemStack(SkyBlockRegister.LAND_FLAG_BLOCK));
            content.add(new ItemStack(SkyBlockRegister.CAMPFIRE_BLOCK));
            content.add(new ItemStack(SkyBlockRegister.LARGE_CAMP_KIT));
            content.add(new ItemStack(SkyBlockRegister.MEDIUM_CAMP_KIT));
            content.add(new ItemStack(SkyBlockRegister.SMALL_CAMP_KIT));
            content.add(new ItemStack(SkyBlockRegister.CREEPER_BLOCK)); // 크리퍼 블록 추가
        });

        // 스킬 등록
        registerSkills();

        // 레벨업 이벤트 리스너 등록
        SkillComponentImpl.SKILL_LEVEL_UP.register((player, skillName, newLevel) -> {
            player.sendMessage(Text.literal("축하합니다! " + skillName + " 스킬이 " + newLevel + "레벨이 되었습니다!"), false);
        });
    }

    private void registerSkills() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            SkillComponent skills = SKILLS.get(player);
            skills.registerSkill("farming", 100);
            skills.registerSkill("camping", 50);
            skills.registerSkill("fishing", 75);
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("testskills")
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        SkillComponent skills = SKILLS.get(player);
                        if (skills != null) {
                            context.getSource().sendFeedback(() -> Text.literal("스킬 정보:"), false);
                            for (String skillName : skills.getAllSkillLevels().keySet()) {
                                int level = skills.getSkillLevel(skillName);
                                context.getSource().sendFeedback(() -> Text.literal(skillName + ": " + level), false);
                            }
                        } else {
                            context.getSource().sendFeedback(() -> Text.literal("스킬 컴포넌트를 찾을 수 없습니다."), false);
                        }
                        return 1;
                    }));
        });
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {

        entityComponentFactoryRegistry.registerFor(PlayerEntity.class, SKILLS, SkillComponentImpl::new);
        //entityComponentFactoryRegistry.beginRegistration(PlayerEntity.class, SKILLS).after(SKILLS).end(SkillComponentImpl::new);
    }
}