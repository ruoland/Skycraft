package org.land.skycraftclient;

import com.google.common.collect.Lists;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.land.skycraftclient.db.Skill;
import org.land.skycraftclient.skill.Skills;
import tschipp.carryon.CarryOnCommon;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;

import java.util.Iterator;
import java.util.List;




public class ElytraEvent {

    private static final int COLLISION_TICK = 5;//5틱에 한번만 겉날개 넉백 처리 계산함
    private int tick = 0;

    public void register() {
        ServerTickEvents.START_SERVER_TICK.register(
                server -> {
                    tick++;
                    for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if(player.isFallFlying()) {
                            checkEvent(player);
                        }
                    }

                }
        );


    }

    public void checkEvent(PlayerEntity player) {
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
}

