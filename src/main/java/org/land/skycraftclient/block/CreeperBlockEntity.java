package org.land.skycraftclient.block;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.land.skycraftclient.register.ModBlockEntities;

public class CreeperBlockEntity extends BlockEntity {
    private float scale = 0, yaw = 0;
    public CreeperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREEPER_BLOCK_ENTITY, pos, state);
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        nbt.putFloat("scale", scale);
        nbt.putFloat("yaw", yaw);
        System.out.println(scale +" 입력됨");
    }
    public void sync() {
        if (world != null && !world.isClient()) {
            BlockEntityUpdateS2CPacket packet = BlockEntityUpdateS2CPacket.create(this);
            PlayerLookup.tracking(this).forEach(player ->
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(packet));
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        sync();
    }
    public void setYaw(float yaw) {
        this.yaw = yaw;
        markDirty();
    }

    public float getYaw() {
        return yaw;
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {

        return createNbt(registryLookup);
    }

    public CreeperBlockEntity setScale(float scale) {
        this.scale = scale;
        markDirty();
        return this;
    }

    public float getSize(){

        return scale;
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        scale = nbt.getFloat("scale");
        yaw = nbt.getFloat("yaw");
    }
}

