package org.land.skycraftclient.client.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public interface IPacketHandler {

    Identifier ANIMATION_CHANNEL = Identifier.of("skycraftclient:animation");
    Identifier KEY_CHANNEL = Identifier.of("skycraftclient:key");
    default void register() {
        registerServerToClient();;
        registerClientToServer();

    }

    //리시브
    void registerServerToClient();

    //클라에서 서버로
    void registerClientToServer();

}
