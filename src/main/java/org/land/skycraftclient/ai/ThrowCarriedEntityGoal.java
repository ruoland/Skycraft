package org.land.skycraftclient.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
public class ThrowCarriedEntityGoal extends Goal {
    private final EndermanEntity enderman;
    private LivingEntity target;
    private final CarryHostileEntityGoal carryGoal;

    public ThrowCarriedEntityGoal(EndermanEntity enderman, CarryHostileEntityGoal carryGoal) {
        this.enderman = enderman;
        this.carryGoal = carryGoal;
    }

    @Override
    public boolean canStart() {
        if (!enderman.hasPassengers() || enderman.getTarget() == null) return false;

        this.target = enderman.getTarget();
        LivingEntity carriedEntity = (LivingEntity) enderman.getFirstPassenger();

        // 플레이어를 들고 있을 때는 던지지 않음
        if (carriedEntity instanceof PlayerEntity) {
            return false;
        }

        // 크리퍼가 폭발하려고 할 때 즉시 던지기
        if (carriedEntity instanceof CreeperEntity) {
            CreeperEntity creeper = (CreeperEntity) carriedEntity;
            if (creeper.isIgnited() || creeper.isFireImmune()) {
                return true;
            }
        }

        return true;
    }



    @Override
    public void start() {
        if (enderman.hasPassengers()) {
            LivingEntity carriedEntity = (LivingEntity) enderman.getFirstPassenger();
            if (carriedEntity != null) {
                carriedEntity.stopRiding();

                Vec3d throwDirection = target.getPos().subtract(enderman.getPos()).normalize();
                carriedEntity.setVelocity(throwDirection.multiply(1.5));


                enderman.setCarriedBlock(null);

                // 던진 엔티티 기록
                carryGoal.addThrownEntity(carriedEntity.getUuid());

                // 크리퍼를 던진 경우 즉시 텔레포트
                if (carriedEntity instanceof CreeperEntity) {
                    customTeleportRandomly();
                }
            }
        }
    }
    public void customTeleportRandomly() {
        if (!enderman.getWorld().isClient() && enderman.isAlive()) {
            double d0 = enderman.getX() + (enderman.getRandom().nextDouble() - 0.5D) * 64.0D;
            double d1 = enderman.getY() + (double)(enderman.getRandom().nextInt(64) - 32);
            double d2 = enderman.getZ() + (enderman.getRandom().nextDouble() - 0.5D) * 64.0D;
            enderman.teleport(d0, d1, d2, true);
        }
    }

    @Override
    public boolean shouldContinue() {
        return false; // 한 번만 실행
    }
}
