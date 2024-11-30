package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.land.skycraftclient.client.packet.AnimationPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import org.land.skycraftclient.client.packet.AnimationPayload;

public class SkycraftclientClient implements ClientModInitializer {

    public static final AnimationPacketHandler ANIMATION_HANDLER= new AnimationPacketHandler();
    public static final AttackHandler ATTACK_HANDLER = new AttackHandler();

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(AnimationPayload.ID, AnimationPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AnimationPayload.ID, AnimationPayload.CODEC);
        ANIMATION_HANDLER.register();
        ATTACK_HANDLER.register();
        ClientEvent clientEvent = new ClientEvent();
        clientEvent.register();
    }
}
