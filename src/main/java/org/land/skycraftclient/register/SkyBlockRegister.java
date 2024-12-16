package org.land.skycraftclient.register;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.IslandFlagBlock;

public class SkyBlockRegister {
    public static final IslandFlagBlock LAND_FLAG_BLOCK = register("landflag", new IslandFlagBlock(AbstractBlock.Settings.create()));

    private static <T extends Block> T register(String path, T block){
        Registry.register(Registries.BLOCK, Identifier.of("skycraftclient", path), block);
        return block;
    }
}
