package org.land.skycraftclient.client.mixin.client;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.ToIntFunction;

@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor {
    @Accessor("settings")
    void setSettings(AbstractBlock.Settings settings);
}