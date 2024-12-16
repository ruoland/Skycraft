package org.land.skycraftclient;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.AbstractBlock;
import org.land.skycraftclient.register.SkyBlockEntityRegister;

public class Skycraftclient implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerEvent serverEvent = new ServerEvent();
        serverEvent.register();

        SkyBlockEntityRegister.initialize();



    }

}
