package org.land.skycraftclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.land.skycraftclient.CarryHostileEntityGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MobEntity {

    protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addCustomCarryGoal(CallbackInfo ci) {
        // 새로운 AI 태스크 추가
        this.goalSelector.add(1, new CarryHostileEntityGoal((EndermanEntity) (Object) this));
    }


    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        return super.getPassengerRidingPos(passenger);
    }

    @Inject(method = "getPassengerRidingPos", at = @At("HEAD"), cancellable = true)
    private void modifyPassengerRidingPos(Entity passenger, CallbackInfoReturnable<Vec3d> cir) {
        // 'this'는 EndermanEntity를 참조합니다
        EndermanEntity enderman = (EndermanEntity)(Object)this;

        // 엔더맨 앞에 탑승자를 위치시킵니다
        Vec3d forward = Vec3d.fromPolar(0, enderman.getYaw()).multiply(0.5); // 0.5블록 앞으로
        Vec3d newPos = enderman.getPos().add(forward).add(0, 1.5, 0); // 높이도 조정

        cir.setReturnValue(newPos);
    }
}
