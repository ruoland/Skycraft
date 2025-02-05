package org.land.skycraftclient.island.boundary;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.land.skycraftclient.register.SkyBlockRegister;

import java.util.Set;

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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if(!world.isClient) {
            IslandFlagBlockEntity block = (IslandFlagBlockEntity) world.getBlockEntity(pos);
            Set<BlockPos> pair = IslandBoundaryDetector.detectIslandBoundary((ServerWorld) world, pos);
            IslandBoundaryDetector.createBoundingBox((ServerWorld) world, pair);

        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, SkyBlockRegister.LAND_FLAG_ENTITY,
                IslandFlagBlockEntity::tick);
    }

}
