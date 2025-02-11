package org.land.skycraftclient.ai;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.Monster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CarryHostileEntityGoal extends Goal {
    private final EndermanEntity enderman;
    private LivingEntity target;
    private final Set<UUID> thrownEntities = new HashSet<>();
    public CarryHostileEntityGoal(EndermanEntity enderman) {
        this.enderman = enderman;
    }

    @Override
    public boolean canStart() {
        if (enderman.hasPassengers()) return false;

        List<LivingEntity> nearbyEntities = enderman.getWorld().getEntitiesByClass(
                LivingEntity.class,
                enderman.getBoundingBox().expand(5.0),
                entity -> entity instanceof Monster && entity.isAlive()
                        && entity.getPassengerList().isEmpty()
                        && entity.getControllingPassenger() == null
                        && entity.getControllingVehicle() == null
                        && entity.getFirstPassenger() == null
                        && !(entity instanceof EndermanEntity)
                        && !thrownEntities.contains(entity.getUuid()) // 던진 엔티티 제외
        );

        if (!nearbyEntities.isEmpty()) {
            this.target = nearbyEntities.get(0);
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (target != null) {
            // 타겟을 엔더맨에 탑승시킴
            target.startRiding(enderman, true);
            enderman.setCarriedBlock(Blocks.BARRIER.getDefaultState());
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
            enderman.setCarriedBlock(null);
        }
    }
    public void addThrownEntity(UUID entityId) {
        thrownEntities.add(entityId);
    }
}
