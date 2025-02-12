package org.land.skycraftclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.land.skycraftclient.ai.SpiderRiderAI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SpiderEntity.class)
public abstract class SpiderMixin {
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addSpiderRiderGoal(CallbackInfo ci) {
        SpiderEntity spider = (SpiderEntity) (Object) this;

        // 새로운 AI를 추가
        // Accessor를 통해 goalSelector 가져오기
        MobEntityAccessor accessor = (MobEntityAccessor) spider;
        accessor.getGoalSelector().add(2, new SpiderRiderAI(spider));
    }

}
