package org.land.skycraftclient.skill;


import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.land.skycraftclient.SkyPlayer;
import org.land.skycraftclient.db.Skill;

public class BoostSkill extends Skill {
    @Override
    public void onUse(SkyPlayer player) {
        System.out.println("사용됨");

    }
}
