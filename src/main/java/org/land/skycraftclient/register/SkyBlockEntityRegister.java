package org.land.skycraftclient.register;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.IslandFlagBlockEntity;

public class SkyBlockEntityRegister {
    private static <T extends BlockEntityType<?>>   T register(String path, T blockEntityType){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("skycraftclient", path), blockEntityType);
    }
    public static final BlockEntityType<IslandFlagBlockEntity> LAND_FLAG_ENTITY = register(
            "my_land",
            BlockEntityType.Builder.create(
                    (pos, state) -> new IslandFlagBlockEntity(pos, state),
                    SkyBlockRegister.LAND_FLAG_BLOCK
            ).build(null)
    );


    public static void initialize(){

    }


}
