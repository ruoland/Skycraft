package org.land.skycraftclient.register;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.block.CreeperBlockEntity;

public class ModBlockEntities {
    public static BlockEntityType<CreeperBlockEntity> CREEPER_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier.of("skycraftclient", "creeper_block_entity"),
            BlockEntityType.Builder.create(CreeperBlockEntity::new, SkyBlockRegister.CREEPER_BLOCK).build()
    );


}
