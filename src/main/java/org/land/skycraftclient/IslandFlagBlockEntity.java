package org.land.skycraftclient;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.land.skycraftclient.register.SkyBlockEntityRegister;

public class IslandFlagBlockEntity extends BlockEntity {
    public IslandFlagBlockEntity(BlockPos pos, BlockState state) {
        super(SkyBlockEntityRegister.LAND_FLAG_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, IslandFlagBlockEntity be) {
        // 여기에 매 틱마다 실행될 로직을 구현합니다.
    }
}
