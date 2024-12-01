package org.land.skycraftclient;

import net.fabricmc.api.ModInitializer;

public class Skycraftclient implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerEvent serverEvent = new ServerEvent();
        serverEvent.register();
    }
}
