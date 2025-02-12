package org.land.skycraftclient;

import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import org.land.skycraftclient.db.DBManager;
import org.land.skycraftclient.register.SkyBlockRegister;

import org.land.skycraftclient.skill.PlayerSkill;
import org.land.skycraftclient.skill.Skills;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Skycraftclient implements ModInitializer, EntityComponentInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Skycraftclient.class);
    public static MinecraftServer server;
    private static final DBManager dbManager = new DBManager("localhost", 3306, "skycraft_client", "root", "135790");
    private static final Map<UUID, SkyPlayer> skyPlayerMap = new HashMap<>();
    @Override
    public void onInitialize() {
        ElytraEvent elytraEvent = new ElytraEvent();
        elytraEvent.register();
        SkyBlockRegister.initialize();


        // 아이템 그룹에 캠프 관련 아이템 추가
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.add(new ItemStack(SkyBlockRegister.CREEPER_BLOCK)); // 크리퍼 블록 추가
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            DevTestCommand.register(dispatcher);
        });
        // 스킬 등록

        try {
            dbManager.initDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ServerPlayerEvents.AFTER_RESPAWN.register((serverPlayerEntity, serverPlayerEntity1, b) -> {
            TrinketsApi.getTrinketComponent(serverPlayerEntity1).ifPresent(trinketComponent -> {
                // Trinket 인벤토리 가져오기
                Map<String, Map<String, TrinketInventory>> inventory = trinketComponent.getInventory();

                // "chest:cape" 슬롯에 접근
                String group = "chest";
                String slot = "cape";

                if (inventory.containsKey(group)) {
                    Map<String, TrinketInventory> groupInventory = inventory.get(group);

                    if (groupInventory.containsKey(slot)) {
                        TrinketInventory slotInventory = groupInventory.get(slot);

                        // 현재 슬롯이 비어 있는지 확인
                        if (slotInventory.isEmpty()) {
                            // 겉날개(ItemStack) 생성
                            ItemStack elytra = new ItemStack(Items.ELYTRA);

                            // 슬롯에 겉날개 장착
                            slotInventory.setStack(0, elytra); // 0번 인덱스는 해당 슬롯의 첫 번째 아이템
                        }
                    }
                }
            });
        });
        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
                    PlayerEntity player = serverPlayNetworkHandler.player;
                    UUID uuid = serverPlayNetworkHandler.player.getUuid();

                    if (dbManager.getPlayer(uuid) == null) {
                        dbManager.insertPlayer(uuid, 1, 0, null);

                    }
                    skyPlayerMap.put(uuid, dbManager.getPlayer(uuid));
                    skyPlayerMap.get(uuid).learnSkill(Skills.ELYTRA_BOOST);
                    skyPlayerMap.get(uuid).learnSkill(Skills.ELYTRA_SPEED);

                });
        ServerLifecycleEvents.AFTER_SAVE.register((minecraftServer, b, b1) -> {
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                SkyPlayer skyPlayer = getDB().getPlayer(player.getUuid());
                for (PlayerSkill skill : skyPlayer.getPlayerSkills().getAllSkills()) {
                    getDB().updatePlayerSkill(player.getUuid(), skill);
                }
            }
        });



    }

    public static SkyPlayer getPlayer(UUID uuid){
        return skyPlayerMap.get(uuid);
    }

    public static DBManager getDB() {
        return dbManager;
    }
    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry entityComponentFactoryRegistry) {

        //entityComponentFactoryRegistry.beginRegistration(PlayerEntity.class, SKILLS).after(SKILLS).end(SkillComponentImpl::new);
    }
}