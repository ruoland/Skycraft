package org.land.skycraftclient.client.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.land.skycraftclient.client.animation.AnimationHandler;
import org.land.skycraftclient.client.animation.AnimationSetting;
import org.land.skycraftclient.client.animation.Animations;


import java.util.UUID;

public class AnimationPacketHandler implements IPacketHandler {


    @Override
    public void registerServerToClient() {
        ClientPlayNetworking.registerGlobalReceiver(AnimationPayload.ID, (payload, context) -> {
            String messageString = payload.message(); //플러그인에서는 UUID 먼저 전송하지만, 어째서인지 여기서는 순서가 바뀌어 전달 됨.
            String uuidString = payload.uuid(); // 아마도 팝 팝아웃 방식? 이라기엔 콤보가 늦게 전송 됨
            UUID uuid = UUID.fromString(uuidString);
            MinecraftClient minecraftClient = context.client();
            try {
                EnumMessageType messageType = EnumMessageType.valueOf(messageString);

                minecraftClient.execute(() -> {
                    AnimationSetting.AnimationSet animationSet = Animations.getAnimation(messageString);
                    AnimationHandler.playAnimation(minecraftClient.world.getPlayerByUuid(uuid), animationSet, 0);
                });
            }catch (IllegalArgumentException e){
                e.printStackTrace();

                minecraftClient.player.sendMessage(Text.literal(messageString+ " <- 모드에서 이 애니메이션을 재생 하려다가 오류가 발생 하였습니다. 클라이언트에 이 애니메이션이 없습니다. 서버에 오류를 제보해주세요."));
            }
            catch (Exception e){
                e.printStackTrace();;
                minecraftClient.player.sendMessage(Text.literal("애니메이션 재생 중에 알 수 없는 오류가 발생하였습니다."));
            }
        });
    }


    @Override
    public void registerClientToServer() {

    }


}
