package org.land.skycraftclient.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.land.skycraftclient.Skycraftclient;
import org.land.skycraftclient.component.SkillComponent;
import org.land.skycraftclient.register.SkyBlockRegister;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CampStructure {
    public static boolean canPlaceTent(World world, BlockPos pos, TentType type) {
        for (BlockPos blockPos : type.getOccupiedBlocks(pos)) {
            if (!world.getBlockState(blockPos).isAir()) {
                return false;
            }
        }
        return true;
    }

    public static void createTent(World world, BlockPos pos, TentType type, PlayerEntity player) {
        if (canPlaceTent(world, pos, type)) {
            for (Map.Entry<BlockPos, BlockState> entry : type.getStructureBlocks(pos).entrySet()) {
                world.setBlockState(entry.getKey(), entry.getValue());
            }
            // 캠프파이어와 침대 설치
            placeCampfire(world, pos.down(), player);
            placeBed(world, pos.add(0, 0, -1), player);
        }
    }

    private static void placeCampfire(World world, BlockPos pos, PlayerEntity player) {
        // 스킬 레벨 확인
        SkillComponent skills = Skycraftclient.SKILLS.get(player);
        int campingLevel = skills.getSkillLevel("camping");
        if (campingLevel >= 5) { // 예: 캠핑 레벨 5 이상
            world.setBlockState(pos, SkyBlockRegister.CAMPFIRE_BLOCK.getDefaultState());
        }
    }

    private static void placeBed(World world, BlockPos pos, PlayerEntity player) {
        // 스킬 레벨 확인
        SkillComponent skills = Skycraftclient.SKILLS.get(player);
        int campingLevel = skills.getSkillLevel("camping");
        if (campingLevel >= 10) { // 예: 캠핑 레벨 10 이상
            world.setBlockState(pos, Blocks.RED_BED.getDefaultState());
        }
    }

    public static void removeTent(World world, BlockPos pos, TentType type) {
        for (BlockPos blockPos : type.getOccupiedBlocks(pos)) {
            world.removeBlock(blockPos, false);
        }
    }

    public enum TentType {
        SMALL, MEDIUM, LARGE;

        public List<BlockPos> getOccupiedBlocks(BlockPos origin) {
            List<BlockPos> blocks = new ArrayList<>();
            switch (this) {
                case SMALL:
                    for (int y = 0; y < 3; y++) {
                        for (int x = -1; x <= 1; x++) {
                            for (int z = -1; z <= 1; z++) {
                                if (y == 2 && (x != 0 || z != 0)) continue;
                                blocks.add(origin.add(x, y, z));
                            }
                        }
                    }
                    break;
                case MEDIUM:
                    // 중간 크기 텐트의 블록 위치 정의
                    break;
                case LARGE:
                    // 대형 텐트의 블록 위치 정의
                    break;
            }
            return blocks;
        }

        public Map<BlockPos, BlockState> getStructureBlocks(BlockPos origin) {
            Map<BlockPos, BlockState> blocks = new HashMap<>();
            BlockState tentMaterial = Blocks.WHITE_WOOL.getDefaultState();
            
            switch (this) {
                case SMALL:
                    for (BlockPos pos : getOccupiedBlocks(origin)) {
                        blocks.put(pos, tentMaterial);
                    }
                    // 입구 만들기
                    blocks.put(origin.add(0, 0, -1), Blocks.AIR.getDefaultState());
                    blocks.put(origin.add(0, 1, -1), Blocks.AIR.getDefaultState());
                    break;
                case MEDIUM:
                    // 중간 크기 텐트 구조 정의
                    break;
                case LARGE:
                    // 대형 텐트 구조 정의
                    break;
            }
            return blocks;
        }
    }
}
