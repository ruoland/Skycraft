package org.land.skycraftclient.client.packet;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AnimationPayload(String message, String uuid) implements CustomPayload {
    static Identifier ANIMATION_CHANNEL = Identifier.of("skycraftclient:animation");
    public static final CustomPayload.Id<AnimationPayload> ID = new CustomPayload.Id<>(ANIMATION_CHANNEL);

    public static final PacketCodec<RegistryByteBuf, AnimationPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, AnimationPayload::message, PacketCodecs.STRING, AnimationPayload::uuid, AnimationPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}