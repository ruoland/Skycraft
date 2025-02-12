package org.land.skycraftclient.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SpiderRiderAI extends Goal {
    private final SpiderEntity spider;
    private LivingEntity target;
    private int cooldown = 0;
    private static final int SEARCH_COOLDOWN = 200; // 10초 (20 틱 * 10)

    public SpiderRiderAI(SpiderEntity spider) {
        this.spider = spider;
    }

    @Override
    public boolean canStart() {

        if (this.spider.hasPassengers() || cooldown > 0 || spider.getLookControl().isLookingAtSpecificPosition() || spider.getAttacking() != null || spider.getAttacker() != null)
            return false;

        if (this.spider.getRandom().nextInt(50) != 0) { // 랜덤성 추가
            return false;
        }

        List<MobEntity> nearbyEntities = this.spider.getWorld().getEntitiesByClass(
                MobEntity.class,
                this.spider.getBoundingBox().expand(10.0),
                entity -> !(entity instanceof SpiderEntity) && entity.isAlive()
        );

        if (!nearbyEntities.isEmpty()) {
            this.target = nearbyEntities.get(this.spider.getRandom().nextInt(nearbyEntities.size()));
            return true;
        }

        cooldown = SEARCH_COOLDOWN;
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive() && !this.spider.hasPassengers();
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.spider.getNavigation().startMovingTo(this.target, 1.0);
        }
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        if (this.target != null) {
            if (this.spider.squaredDistanceTo(this.target) < 2.0) {
                if (!this.target.hasVehicle()) {
                    this.target.startRiding(this.spider);
                }
            } else {
                this.spider.getNavigation().startMovingTo(this.target, 1.0);
            }

            if (this.target instanceof CreeperEntity creeper && creeper.isIgnited()) {
                Vec3d throwDirection = this.spider.getRotationVector().multiply(1.1); // 거미의 바라보는 방향으로 던짐
                creeper.stopRiding();
                creeper.setVelocity(throwDirection.add(0, 0.01, 0)); // 약간의 수직 속도 추가
                this.target = null; // 타겟 초기화
            }
        }
    }

    @Override
    public void stop() {
        this.target = null;
        this.spider.getNavigation().stop();
        cooldown = SEARCH_COOLDOWN;
    }
}
