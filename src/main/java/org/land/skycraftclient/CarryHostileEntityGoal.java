package org.land.skycraftclient;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.Monster;

import java.util.List;

public class CarryHostileEntityGoal extends Goal {
    private final EndermanEntity enderman;
    private LivingEntity target;

    public CarryHostileEntityGoal(EndermanEntity enderman) {
        this.enderman = enderman;
    }

    @Override
    public boolean canStart() {
        // 엔더맨이 이미 다른 엔티티를 들고 있지 않은 경우
        if (enderman.hasPassengers()) return false;

        // 근처의 적대적 엔티티 검색
        List<LivingEntity> nearbyEntities = enderman.getWorld().getEntitiesByClass(
            LivingEntity.class,
            enderman.getBoundingBox().expand(5.0), // 탐지 범위
            entity -> entity instanceof Monster && entity.isAlive() && entity.getPassengerList().isEmpty() && entity.getControllingPassenger() == null && entity.getControllingVehicle() == null && entity.getFirstPassenger() == null && !(entity instanceof EndermanEntity)
        );

        if (!nearbyEntities.isEmpty()) {
            this.target = nearbyEntities.get(0); // 첫 번째 적대적 엔티티 선택
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (target != null) {
            // 타겟을 엔더맨에 탑승시킴
            target.startRiding(enderman, true);
        }
    }

    @Override
    public boolean shouldContinue() {
        // 타겟이 여전히 탑승 중인지 확인
        return target != null && target.hasVehicle() && target.getVehicle() == enderman;
    }

    @Override
    public void stop() {
        if (target != null && target.hasVehicle()) {
            target.stopRiding();
            target = null;
        }
    }
}
