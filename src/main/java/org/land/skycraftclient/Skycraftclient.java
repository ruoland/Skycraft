package org.land.skycraftclient;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

import org.land.skycraftclient.db.DBManager;
import org.land.skycraftclient.register.ModBlockEntities;
import org.land.skycraftclient.register.SkyBlockRegister;
import org.land.skycraftclient.skill.PlayerSkill;
import org.land.skycraftclient.skill.Skills;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Skycraftclient implements ModInitializer, EntityComponentInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Skycraftclient.class);
    public static MinecraftServer server;
    private static final DBManager dbManager = new DBManager();
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
            Skills.init();;
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        ServerPlayConnectionEvents.JOIN.register((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            UUID uuid = serverPlayNetworkHandler.player.getUuid();

            if (dbManager.getPlayer(uuid) == null) {
                dbManager.insertPlayer(uuid, 1, 0, null);
            }
            skyPlayerMap.put(uuid, dbManager.getPlayer(uuid));
        });
        ServerLifecycleEvents.AFTER_SAVE.register((minecraftServer, b, b1) -> {
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                SkyPlayer skyPlayer = getDB().getPlayer(player.getUuid());
                for (PlayerSkill skill : skyPlayer.getPlayerSkills().getAllSkills()) {
                    getDB().updatePlayerSkill(player.getUuid(), skill);
                }
            }
        });

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {

            if(playerEntity.hasPassengers()){
                if(playerEntity.getFirstPassenger() != null){
                    playerEntity.getFirstPassenger().dismountVehicle();
                }
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
                if (!playerEntity.isSneaking()) {
                    entity.stopRiding();

                } else if(playerEntity.isSneaking()){
                    entity.startRiding(playerEntity, true);
                }
            
            return ActionResult.SUCCESS;
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