package org.land.skycraftclient.ai;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
public class FlyingEndermanGoal extends Goal {
    private final EndermanEntity enderman;
    private BlockPos targetPos;
    private int pathfindingCooldown = 0;
    private static final int PATH_RECALCULATION_INTERVAL = 40;
    private static final int MAX_FLIGHT_DURATION = 200; // 10초
    private int flightDuration = 0;
    private boolean isFlying = false;
    private int groundedTime = 0;
    private static final int MIN_GROUNDED_TIME = 200; // 10초
    private static final int MAX_GROUNDED_TIME = 600; // 30초
    private static final float FLIGHT_SPEED = 0.1f;
    private static final float GROUND_SPEED = 0.3f;

    public FlyingEndermanGoal(EndermanEntity enderman) {
        this.enderman = enderman;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return !isFlying && groundedTime <= 0 && (enderman.getRandom().nextFloat() < 0.005f || enderman.getTarget() != null);
    }

    @Override
    public boolean shouldContinue() {
        return isFlying && flightDuration < MAX_FLIGHT_DURATION;
    }

    @Override
    public void start() {
        isFlying = true;
        flightDuration = 0;
        updateTargetPos();
    }

    @Override
    public void stop() {
        isFlying = false;
        targetPos = null;
        flightDuration = 0;
        groundedTime = enderman.getRandom().nextBetween(MIN_GROUNDED_TIME, MAX_GROUNDED_TIME);
    }

    @Override
    public void tick() {
        if (isFlying) {
            flyingTick();
        } else {
            groundedTick();
        }
    }

    private void flyingTick() {
        flightDuration++;
        LivingEntity target = enderman.getTarget();

        if (target != null) {
            targetPos = target.getBlockPos();
        } else if (targetPos == null || pathfindingCooldown <= 0 || enderman.squaredDistanceTo(Vec3d.ofCenter(targetPos)) < 4.0) {
            updateTargetPos();
        }

        if (targetPos != null) {
            double dx = targetPos.getX() + 0.5 - enderman.getX();
            double dy = targetPos.getY() + 0.1 - enderman.getY();
            double dz = targetPos.getZ() + 0.5 - enderman.getZ();
            Vec3d motion = new Vec3d(dx, dy, dz).normalize();

            enderman.setVelocity(motion.multiply(FLIGHT_SPEED));
            enderman.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5));

            if (!enderman.getWorld().isAir(enderman.getBlockPos())) {
                enderman.setVelocity(enderman.getVelocity().add(0, 0.1, 0));
            }
        }

        // 공중에서 짧게 움직이기
        if (enderman.getRandom().nextFloat() < 0.1f) {
            Vec3d randomMotion = new Vec3d(
                    enderman.getRandom().nextDouble() - 0.5,
                    (enderman.getRandom().nextDouble() - 0.5) * 0.5,
                    enderman.getRandom().nextDouble() - 0.5
            ).normalize().multiply(FLIGHT_SPEED * 0.5);
            enderman.setVelocity(enderman.getVelocity().add(randomMotion));
        }

        pathfindingCooldown--;
    }

    private void groundedTick() {
        if (groundedTime > 0) {
            groundedTime--;
        }

        // 가끔 천천히 주변을 움직임
        if (enderman.getRandom().nextFloat() < 0.05f) {
            BlockPos wanderTarget = enderman.getBlockPos().add(
                    enderman.getRandom().nextInt(5) - 2,
                    0,
                    enderman.getRandom().nextInt(5) - 2
            );
            enderman.getNavigation().startMovingTo(wanderTarget.getX(), wanderTarget.getY(), wanderTarget.getZ(), GROUND_SPEED);
        }
    }

    private void updateTargetPos() {
        if (enderman.getTarget() != null) {
            targetPos = enderman.getTarget().getBlockPos();
        } else {
            BlockPos pos = enderman.getBlockPos();
            int range = 8;
            for (int i = 0; i < 10; i++) {
                BlockPos newPos = pos.add(
                        enderman.getRandom().nextInt(range * 2) - range,
                        enderman.getRandom().nextInt(range) - range / 2,
                        enderman.getRandom().nextInt(range * 2) - range
                );
                if (enderman.getWorld().isAir(newPos)) {
                    targetPos = newPos;
                    break;
                }
            }
            if (targetPos == null) {
                targetPos = pos.up(2);
            }
        }
        pathfindingCooldown = PATH_RECALCULATION_INTERVAL;
    }
}
