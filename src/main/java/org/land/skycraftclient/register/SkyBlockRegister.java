package org.land.skycraftclient.register;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.land.skycraftclient.block.CreeperBlock;

public class SkyBlockRegister {
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


    public static void initialize(){

    }
}