package org.land.skycraftclient.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

public class GrabAndAttackPlayerGoal extends Goal {
    private final EndermanEntity enderman;
    private PlayerEntity targetPlayer;
    private int grabCooldown = 0;
    private static final int GRAB_COOLDOWN_TIME = 200; // 10초 (20 틱 * 10)

    public GrabAndAttackPlayerGoal(EndermanEntity enderman) {
        this.enderman = enderman;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (grabCooldown > 0) {
            grabCooldown--;
            return false;
        }
        
        LivingEntity target = this.enderman.getTarget();
        if (!(target instanceof PlayerEntity)) {
            return false;
        }
        
        this.targetPlayer = (PlayerEntity) target;
        return true;
    }

    @Override
    public void start() {
        // 플레이어 주변으로 텔레포트
        if (teleportToPlayer()) {
            // 텔레포트 성공 시 플레이어를 잡음
            grabPlayer();
        }
    }

    @Override
    public void tick() {
        if (targetPlayer == null || !targetPlayer.isAlive()) {
            stop();
            return;
        }

        if (enderman.hasPassenger(targetPlayer)) {
            // 플레이어를 잡고 있는 경우 공격
            enderman.tryAttack(targetPlayer);
        } else {
            // 플레이어를 놓친 경우 다시 잡으려고 시도
            if (enderman.squaredDistanceTo(targetPlayer) < 4.0) { // 2블록 이내
                grabPlayer();
            } else {
                enderman.getNavigation().startMovingTo(targetPlayer, 1.0);
            }
        }
    }

    @Override
    public void stop() {
        if (enderman.hasPassenger(targetPlayer)) {
            targetPlayer.stopRiding();
        }
        targetPlayer = null;
        grabCooldown = GRAB_COOLDOWN_TIME;
    }

    private boolean teleportToPlayer() {
        for (int i = 0; i < 16; i++) { // 16번 시도
            double d0 = targetPlayer.getX() + (enderman.getRandom().nextDouble() - 0.5D) * 5.0D;
            double d1 = targetPlayer.getY() + enderman.getRandom().nextInt(3) - 1;
            double d2 = targetPlayer.getZ() + (enderman.getRandom().nextDouble() - 0.5D) * 5.0D;
            if (enderman.teleport(d0, d1, d2, true)) {
                return true;
            }
        }
        return false;
    }

    private void grabPlayer() {
        targetPlayer.startRiding(enderman, true);
    }
}
