package org.land.skycraftclient.client;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface ISkyAnimatedPlayer {
    /**
     * Use your mod ID in the method name to avoid collisions with other mods
     * @return Mod animation container
     */
    ModifierLayer<IAnimation> skycraft_getModAnimation();

    ModifierLayer<IAnimation> skycraft_getMirrorAnimation();
}