package org.land.skycraftclient.register;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.block.CampKitItem;
import org.land.skycraftclient.block.CampStructure;
import org.land.skycraftclient.block.CampfireBlock;
import org.land.skycraftclient.block.CreeperBlock;
import org.land.skycraftclient.island.boundary.IslandFlagBlock;
import org.land.skycraftclient.island.boundary.IslandFlagBlockEntity;

public class SkyBlockRegister {
    public static final IslandFlagBlock LAND_FLAG_BLOCK = register("landflag", new IslandFlagBlock(AbstractBlock.Settings.create()));
    public static final CampfireBlock CAMPFIRE_BLOCK = register("campfire", new CampfireBlock(AbstractBlock.Settings.create().strength(2.0f)));
    public static final Item SMALL_CAMP_KIT = registerItem("small_camp_kit", new CampKitItem(new Item.Settings().maxCount(1), CampStructure.TentType.SMALL));
    public static final Item MEDIUM_CAMP_KIT = registerItem("medium_camp_kit", new CampKitItem(new Item.Settings().maxCount(1), CampStructure.TentType.MEDIUM));
    public static final Item LARGE_CAMP_KIT = registerItem("large_camp_kit", new CampKitItem(new Item.Settings().maxCount(1), CampStructure.TentType.LARGE));
    public static final Block CREEPER_BLOCK = register("creeper_block", new CreeperBlock(AbstractBlock.Settings.create().strength(1.5f, 6.0f)));
    private static <T extends Block> T register(String path, T block) {
        T registeredBlock = Registry.register(Registries.BLOCK, Identifier.of("skycraftclient", path), block);
        Registry.register(Registries.ITEM, Identifier.of("skycraftclient", path), new BlockItem(registeredBlock, new Item.Settings()));
        return registeredBlock;
    }

    private static Item registerItem(String path, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of("skycraftclient", path), item);
    }
    private static <T extends BlockEntityType<?>>   T register(String path, T blockEntityType){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("skycraftclient", path), blockEntityType);
    }
    public static final BlockEntityType<IslandFlagBlockEntity> LAND_FLAG_ENTITY = register(
            "my_land",
            BlockEntityType.Builder.create(
                    IslandFlagBlockEntity::new,
                    SkyBlockRegister.LAND_FLAG_BLOCK
            ).build(null)
    );


    public static void initialize(){

    }
}