package org.land.skycraftclient.island.boundary;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class OldIslandBoundaryDetector {
    private static final int[][] DIRECTIONS = {
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}
    };
    private static final int MAX_SEARCH_DISTANCE = 500; // 조절 가능한 값
    public static Pair<BlockPos, BlockPos> detectIslandBoundary(ServerWorld world, BlockPos startPos) {
        Set<BlockPos> boundary = new HashSet<>();
        ChunkPos startChunk = new ChunkPos(startPos);

        // 시작 청크의 모서리 검사
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    BlockPos edgePos = findHighestNonAirBlock(world, new BlockPos(startChunk.getStartX() + x, 0, startChunk.getStartZ() + z));
                    if (edgePos != null) {
                        boundary.add(edgePos);
                    }
                }
            }
        }

        // 주변 청크 검사
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                if (xOffset == 0 && zOffset == 0) continue; // 시작 청크 제외
                ChunkPos neighborChunk = new ChunkPos(startChunk.x + xOffset, startChunk.z + zOffset);
                searchChunkBorder(world, neighborChunk, boundary);
            }
        }

        // 경계 확장
        expandBoundary(world, boundary);

        // 경계에 블록 설치
        placeBoundaryBlocks(world, boundary);

        // 최소/최대 좌표 계산
        BlockPos minPos = boundary.stream().reduce((a, b) -> new BlockPos(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        )).orElse(startPos);

        BlockPos maxPos = boundary.stream().reduce((a, b) -> new BlockPos(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        )).orElse(startPos);

        return new Pair<>(minPos, maxPos);
    }

    private static void searchChunkBorder(ServerWorld world, ChunkPos chunk, Set<BlockPos> boundary) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x == 0 || x == 15 || z == 0 || z == 15) {
                    BlockPos borderPos = findHighestNonAirBlock(world, new BlockPos(chunk.getStartX() + x, 0, chunk.getStartZ() + z));
                    if (borderPos != null) {
                        boundary.add(borderPos);
                    }
                }
            }
        }
    }

    private static BlockPos findHighestNonAirBlock(ServerWorld world, BlockPos pos) {
        for (int y = world.getTopY(); y >= world.getBottomY(); y--) {
            BlockPos checkPos = new BlockPos(pos.getX(), y, pos.getZ());
            if (!isAir(world, checkPos)) {
                return checkPos;
            }
        }
        return null;
    }

    private static void expandBoundary(ServerWorld world, Set<BlockPos> boundary) {
        Set<BlockPos> newBoundary = new HashSet<>(boundary);
        for (BlockPos pos : boundary) {
            for (int[] dir : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                BlockPos newPos = pos.add(dir[0], 0, dir[1]);
                BlockPos highestPos = findHighestNonAirBlock(world, newPos);
                if (highestPos != null && !boundary.contains(highestPos)) {
                    newBoundary.add(highestPos);
                }
            }
        }
        boundary.addAll(newBoundary);
    }

    private static void placeBoundaryBlocks(ServerWorld world, Set<BlockPos> boundary) {
        Block boundaryBlock = Blocks.GLOWSTONE; // 경계를 표시할 블록
        for (BlockPos pos : boundary) {
            world.setBlockState(pos.up(), boundaryBlock.getDefaultState());
        }
    }



    private static boolean isAir(ServerWorld world, BlockPos pos) {
        System.out.println("Is air: " + world.isAir(pos));
        System.out.println("Is solid: " + world.getBlockState(pos).isSolidBlock(world, pos));

        return world.isAir(pos) || !world.getBlockState(pos).isSolidBlock(world, pos);
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
