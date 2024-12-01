package org.land.skycraftclient;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Iterator;
import java.util.List;

public class ServerEvent {
    public static double elytra_speed_config = 0.6f;
    public static double vertical_acceleration = 0.05; // 수직 가속도 설정
    private static final int COLLISION_TICK = 5;//5틱에 한번만 겉날개 넉백 처리 계산함
    private int tick = 0;
    public void register(){
        ServerTickEvents.START_SERVER_TICK.register(
                server -> {
                    tick++;
                    for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if(tick > COLLISION_TICK){
                            checkEvent(player);
                            tick = 0;
                        }
                        if (player.isFallFlying()) {
                            adjustElytraFlight(player);
                        }
                    }
                }
        );
    }
    public void checkEvent(PlayerEntity player){

        {
            Box box;
            if (player.hasVehicle() && !player.getVehicle().isRemoved()) {
                box = player.getBoundingBox().union(player.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0);
            } else {
                box = player.getBoundingBox().expand(1.0, 0.5, 1.0);
            }

            List<Entity> list = player.getWorld().getOtherEntities(player, box);
            List<Entity> list2 = Lists.newArrayList();
            Iterator var5 = list.iterator();

            while (var5.hasNext()) {
                Entity entity = (Entity) var5.next();
                if (entity instanceof LivingEntity livingEntity) {
                    Vec3d vec3d = player.getVelocity().normalize();
                    livingEntity.takeKnockback(1, -vec3d.x, -vec3d.z);
                    System.out.println(vec3d.x + " - " + vec3d.y + " - " + vec3d.z + " - " + vec3d.horizontalLength());
                }
            }

            if (!list2.isEmpty()) {
                Vec3d vec3d = player.getVelocity().normalize();
                Entity entity = (Entity) Util.getRandom(list2, player.getRandom());
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.takeKnockback(0.3, -vec3d.z, -vec3d.x);
                }

            }
        }

    }
    private static final double MAX_SPEED = 1.0;
    private static final double MIN_SPEED = 0.8;
    private static final double SPEED_DECAY_FACTOR = 0.88;
    private static final double SPEED_RECOVERY_FACTOR = 1.03;
    private static final double ACCELERATION_FACTOR = 0.06;
    private static final double LIFT_FACTOR = 0.01;
    private static final double UPWARD_ACCELERATION = 0.06;
    private static final int INITIAL_FLIGHT_TICKS = 60;

    private Vec3d lastVelocity = Vec3d.ZERO;
    private double currentSpeedMultiplier = 1.0;

    public void adjustElytraFlight(PlayerEntity player) {
        Vec3d currentVelocity = player.getVelocity();
        double currentSpeed = currentVelocity.length();
        boolean isHorizontalFlight = isPlayerFlyingHorizontal(currentVelocity);

        // 쉬프트 키 상태에 따라 속도 조절
        if (player.isSneaking()) {
            currentSpeedMultiplier = Math.max(currentSpeedMultiplier * SPEED_DECAY_FACTOR, 0.2);
        } else {
            currentSpeedMultiplier = Math.min(currentSpeedMultiplier * SPEED_RECOVERY_FACTOR, 1.0);
        }

        Vec3d newVelocity;

        if (player.getFallFlyingTicks() < INITIAL_FLIGHT_TICKS) {
            newVelocity = handleInitialFlight(currentVelocity);
        } else if (isHorizontalFlight) {
            newVelocity = handleHorizontalFlight(player, currentVelocity);
        } else if (player.getPitch() < -30) {
            newVelocity = handleUpwardFlight(currentVelocity);
        } else {
            newVelocity = handleOtherFlightConditions(currentVelocity, currentSpeed);
        }

        // 속도 조절 적용
        newVelocity = newVelocity.multiply(currentSpeedMultiplier);

        applyVelocityChange(player, newVelocity);
    }

    private Vec3d handleInitialFlight(Vec3d currentVelocity) {
            double newSpeed = Math.min(currentVelocity.length() + ACCELERATION_FACTOR, MIN_SPEED);
            return currentVelocity.normalize().multiply(newSpeed);
        }

        private Vec3d handleHorizontalFlight(PlayerEntity player, Vec3d currentVelocity) {
            double targetSpeed = Math.min(lastVelocity.length(), MAX_SPEED) * SPEED_DECAY_FACTOR;
            targetSpeed = Math.max(targetSpeed, MIN_SPEED);

            Vec3d horizontalDirection = player.getRotationVector().normalize();
            Vec3d newVelocity = horizontalDirection.multiply(targetSpeed);

            return newVelocity.add(0, LIFT_FACTOR, 0);
        }

        private Vec3d handleUpwardFlight(Vec3d currentVelocity) {
            return new Vec3d(
                    currentVelocity.x,
                    Math.max(currentVelocity.y + UPWARD_ACCELERATION, UPWARD_ACCELERATION),
                    currentVelocity.z
            );
        }

        private Vec3d handleOtherFlightConditions(Vec3d currentVelocity, double currentSpeed) {
            if (currentSpeed < MIN_SPEED) {
                return currentVelocity.normalize().multiply(MIN_SPEED);
            }
            return currentVelocity;
        }
    private void applyVelocityChange(PlayerEntity player, Vec3d newVelocity) {
        Vec3d velocityChange = newVelocity.subtract(player.getVelocity());
        player.addVelocity(velocityChange.x, velocityChange.y, velocityChange.z);
        player.velocityModified = true;
        lastVelocity = newVelocity;
    }


        private boolean isPlayerFlyingHorizontal(Vec3d velocity) {
            double maxElevationAngle = Math.toRadians(30);
            double horizontalSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
            double elevationAngle = Math.atan2(Math.abs(velocity.y), horizontalSpeed);
            return elevationAngle <= maxElevationAngle;
        }
    }


