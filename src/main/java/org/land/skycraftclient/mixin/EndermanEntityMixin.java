package org.land.skycraftclient.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.land.skycraftclient.ai.CarryHostileEntityGoal;
import org.land.skycraftclient.ai.FlyingEndermanGoal;
import org.land.skycraftclient.ai.GrabAndAttackPlayerGoal;
import org.land.skycraftclient.ai.ThrowCarriedEntityGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndermanEntity.class)
public abstract class EndermanEntityMixin extends MobEntity {

    protected EndermanEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initGoals", at = @At("TAIL"))
    private void addCustomCarryGoal(CallbackInfo ci) {
        CarryHostileEntityGoal carryGoal = new CarryHostileEntityGoal((EndermanEntity) (Object) this);
        this.goalSelector.add(1, carryGoal);
        this.goalSelector.add(2, new ThrowCarriedEntityGoal((EndermanEntity) (Object) this, carryGoal));
        this.goalSelector.add(1, new GrabAndAttackPlayerGoal((EndermanEntity) (Object)this));
        this.goalSelector.add(1, new FlyingEndermanGoal((EndermanEntity)(Object)this));
    }

    @Override
    public Vec3d getPassengerRidingPos(Entity passenger) {
        EndermanEntity enderman = (EndermanEntity)(Object)this;
        Vec3d forward = Vec3d.fromPolar(0, enderman.getYaw()).multiply(1.0);
        enderman.setCarriedBlock(Blocks.BARRIER.getDefaultState());

        return enderman.getPos().add(forward).add(0, 0.5, 0);
    }
}
