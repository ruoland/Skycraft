package org.land.skycraftclient;


import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IslandManager {
    //private static final ConcurrentHashMap<UUID, Pair<BlockPos,BlockPos>> ISLAND_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Pair<BlockPos,BlockPos>, UUID> ISLAND_MAP = new ConcurrentHashMap<>();

    //-64~384까지 생성됨,
    public void register(){

        UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
            if(playerEntity.getStackInHand(hand).getItem() == Items.STICK){
                if(world instanceof ServerWorld serverWorld) {
                    BlockPos startPos = playerEntity.getSteppingPos(); // 시작 위치
                    Pair<BlockPos, BlockPos> boundary = IslandBoundaryDetector.detectIslandBoundary(serverWorld, startPos);
                    ISLAND_MAP.put(boundary, playerEntity.getUuid());
                    //IslandBoundaryDetector.createBoundingBox(serverWorld, boundary);
                }
            }
            return TypedActionResult.pass(playerEntity.getStackInHand(hand));
        });



        ServerTickEvents.END_WORLD_TICK.register(serverWorld -> {
            for(ServerPlayerEntity player : serverWorld.getPlayers()){
                player.getChunkPos();
            }
            for (Map.Entry<Pair<BlockPos, BlockPos>, UUID> entry : ISLAND_MAP.entrySet()) {
                BlockPos minPos = entry.getKey().getLeft();
                BlockPos maxPos = entry.getKey().getRight();
                Box box = new Box(
                        minPos.getX(), minPos.getY(), minPos.getZ(),
                        maxPos.getX() + 1, maxPos.getY() + 1, maxPos.getZ() + 1
                );
                List<PlayerEntity> entities = serverWorld.getEntitiesByClass(PlayerEntity.class, box, entity -> true);

            }
        });
    }

    public static boolean isPlayerInIsland(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();

        for (Map.Entry<Pair<BlockPos, BlockPos>, UUID> entry : ISLAND_MAP.entrySet()) {
            Pair<BlockPos, BlockPos> islandBounds = entry.getKey();
            BlockPos startPos = islandBounds.getLeft();
            BlockPos endPos = islandBounds.getRight();

            if (isWithinBounds(playerPos, startPos, endPos)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isWithinBounds(BlockPos pos, BlockPos start, BlockPos end) {
        return pos.getX() >= start.getX() && pos.getX() <= end.getX()
                && pos.getY() >= start.getY() && pos.getY() <= end.getY()
                && pos.getZ() >= start.getZ() && pos.getZ() <= end.getZ();
    }


}
