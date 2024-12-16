package org.land.skycraftclient;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.land.skycraftclient.register.SkyBlockEntityRegister;

//이 블럭이 설치된 곳은 설치한 자의 땅
public class IslandFlagBlock extends BlockWithEntity {
    public IslandFlagBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new IslandFlagBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends IslandFlagBlock> getCodec() {
        return createCodec(IslandFlagBlock::new);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SkyBlockEntityRegister.LAND_FLAG_ENTITY,
                (world1, pos, state1, be) -> IslandFlagBlockEntity.tick(world1, pos, state1, (IslandFlagBlockEntity)be));
    }

}
