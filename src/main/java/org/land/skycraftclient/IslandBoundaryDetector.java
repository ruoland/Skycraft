package org.land.skycraftclient;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IslandBoundaryDetector {
    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
    };
    public static Pair<BlockPos, BlockPos> detectIslandBoundary(ServerWorld world, BlockPos startPos) {
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> boundary = new HashSet<>();
        Set<ChunkPos> checkedChunks = new HashSet<>();

        queue.offer(startPos);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (visited.contains(pos)) continue;
            visited.add(pos);

            ChunkPos chunkPos = new ChunkPos(pos);
            if (checkedChunks.contains(chunkPos)) continue;
            checkedChunks.add(chunkPos);

            boolean isBoundary = false;
            for (int[] dir : DIRECTIONS) {
                BlockPos newPos = pos.add(dir[0], dir[1], dir[2]);
                if (isAir(world, newPos)) {
                    isBoundary = true;
                } else if (!visited.contains(newPos)) {
                    queue.offer(newPos);
                }
            }

            if (isBoundary) boundary.add(pos);
        }

        BlockPos minPos = startPos, maxPos = startPos;

        for (BlockPos pos : boundary) {
            minPos = BlockPos.min(minPos, pos);
            maxPos = BlockPos.max(maxPos, pos);
        }
        return new Pair<>(minPos, maxPos);
    }


    private static boolean isAir(ServerWorld world, BlockPos pos) {
        return world.getBlockState(pos).isAir();
    }

    public static void createBoundingBox(ServerWorld world, Set<BlockPos> boundary) {
        if (boundary.isEmpty()) return;

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : boundary) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        Block boundaryBlock = Blocks.GLASS; // 경계를 표시할 블록

        // 모서리 생성
        for (int x = minX; x <= maxX; x += maxX - minX) {
            for (int y = minY; y <= maxY; y += maxY - minY) {
                for (int z = minZ; z <= maxZ; z += maxZ - minZ) {
                    world.setBlockState(new BlockPos(x, y, z), boundaryBlock.getDefaultState());
                }
            }
        }

        // 모서리를 연결하는 선 생성
        for (int x = minX + 1; x < maxX; x++) {
            world.setBlockState(new BlockPos(x, minY, minZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(x, minY, maxZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(x, maxY, minZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(x, maxY, maxZ), boundaryBlock.getDefaultState());
        }

        for (int y = minY + 1; y < maxY; y++) {
            world.setBlockState(new BlockPos(minX, y, minZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(minX, y, maxZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(maxX, y, minZ), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(maxX, y, maxZ), boundaryBlock.getDefaultState());
        }

        for (int z = minZ + 1; z < maxZ; z++) {
            world.setBlockState(new BlockPos(minX, minY, z), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(minX, maxY, z), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(maxX, minY, z), boundaryBlock.getDefaultState());
            world.setBlockState(new BlockPos(maxX, maxY, z), boundaryBlock.getDefaultState());
        }
    }

}
