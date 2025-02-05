package org.land.skycraftclient.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CampEffects {
    public static void applyRestBonus(ServerWorld world, ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        BlockState campfireState = world.getBlockState(playerPos.down());
        
        if (campfireState.getBlock() instanceof CampfireBlock && campfireState.get(CampfireBlock.LIT)) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 200, 0));
        }
    }
}
