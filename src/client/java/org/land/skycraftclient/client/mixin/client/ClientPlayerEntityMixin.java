package org.land.skycraftclient.client.mixin.client;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.land.skycraftclient.client.ISkyAnimatedPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(AbstractClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements ISkyAnimatedPlayer {

    //Unique annotation will rename private methods/fields if needed to avoid collisions.
    @Unique
    private final ModifierLayer<IAnimation> modAnimationContainer = new ModifierLayer<>();
    private final ModifierLayer<IAnimation> modMirrorAnimationContainer = new ModifierLayer<>();

    /**
     * Add the animation registration to the end of the constructor
     * Or you can use {@link dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess#REGISTER_ANIMATION_EVENT} event for this
     */
    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        //Mixin does not know (yet) that this will be merged with AbstractClientPlayerEntity
        modMirrorAnimationContainer.addModifierBefore(new MirrorModifier(true));
        modAnimationContainer.addModifierBefore(new SpeedModifier(1.0F));
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object)this).addAnimLayer(130000, modAnimationContainer); //Register the layer with a priority
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object) this).addAnimLayer(130000, modMirrorAnimationContainer);
        //The priority will tell, how important is this animation compared to other mods. Higher number means higher priority
        //Mods with higher priority will override the lower priority mods (if they want to animation anything)


    }


    /**
     * Override the interface function, so we can use it in the future
     */
    @Override
    public ModifierLayer<IAnimation> skycraft_getModAnimation() {
        return modAnimationContainer;
    }

    @Override
    public ModifierLayer<IAnimation> skycraft_getMirrorAnimation() {
        return modMirrorAnimationContainer;
    }
}