package org.land.skycraftclient.skill;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.land.skycraftclient.SkyPlayer;
import org.land.skycraftclient.db.Skill;

public class BoostSkill extends Skill {
    @Override
    public void onUse(PlayerEntity player) {

        Vec3d vec3d3;
        if (player.isFallFlying()) {
            Vec3d vec3d = player.getRotationVector();
            double d = 1.5;
            double e = 0.1;
            Vec3d vec3d2 = player.getVelocity();
            player.addVelocity(vec3d.x*1.1, vec3d.y*1.1 , vec3d.z * 1.1);

            System.out.println("사용됨");

            //player.setPosition(player.getX() + vec3d3.x, player.getY() + vec3d3.y, player.getZ() + vec3d3.z);
            //player.setVelocity(player.getVelocity());
        } else {
            vec3d3 = Vec3d.ZERO;
        }


        //player.getWorld().spawnEntity(new FireworkRocketEntity(player.getWorld(), new ItemStack(Items.FIREWORK_ROCKET), player));

    }
}
