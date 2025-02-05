package org.land.skycraftclient.island.boundary;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.land.skycraftclient.Skycraftclient;
import org.land.skycraftclient.register.SkyBlockRegister;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;


public class IslandFlagBlockEntity extends BlockEntity {
    private BlockPos minPos = null, maxPos = null;
    private Box islandFlagBox;
    public IslandFlagBlockEntity(BlockPos pos, BlockState state) {
        super(SkyBlockRegister.LAND_FLAG_ENTITY, pos, state);
    }


    public void setBoxPos(BlockPos maxPos, BlockPos minPos) {
        this.maxPos = maxPos;
        this.minPos = minPos;
        islandFlagBox = new Box(maxPos.toCenterPos(), minPos.toCenterPos());
    }




    public Optional<BlockPos> getMinPos(){
        return Optional.ofNullable(minPos);
    }


    public Optional<BlockPos> getMaxPos() {
        return Optional.ofNullable(maxPos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if(getMaxPos().isPresent()){
            nbt.putInt("minX", maxPos.getX());
            nbt.putInt("minY", maxPos.getY());
            nbt.putInt("minZ", maxPos.getZ());
        }
        if(getMinPos().isPresent()){
            nbt.putInt("minX", minPos.getX());
            nbt.putInt("minY", minPos.getY());
            nbt.putInt("minZ", minPos.getZ());
        }

        if((getMinPos().isPresent() && !getMaxPos().isPresent()) || (!getMinPos().isPresent() && getMaxPos().isPresent()))
        {
            Skycraftclient.LOGGER.error("[섬 경계 에러]최소 좌표, 최대 좌표 둘 중 하나만 비어 있습니다. {}, {}", minPos, maxPos);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if(nbt.contains("minX")){

            int minX = nbt.getInt("minX");
            int minY = nbt.getInt("minY");
            int minZ = nbt.getInt("minZ");
            int maxX = nbt.getInt("minX");
            int maxY = nbt.getInt("minY");
            int maxZ = nbt.getInt("minZ");
            setBoxPos(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
        }

    }

    public static void tick(World world, BlockPos pos, BlockState state, IslandFlagBlockEntity be) {

        if (be.getMinPos().isPresent() && be.getMaxPos().isPresent()) {

            List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, be.islandFlagBox);
            Iterator var11 = list.iterator();
            PlayerEntity playerEntity;
            while (var11.hasNext()) {
                playerEntity = (PlayerEntity) var11.next();
                System.out.println(playerEntity.getName() +" - 감지");
            }
        }
    }

}
