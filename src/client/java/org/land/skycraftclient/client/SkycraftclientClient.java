package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.land.skycraftclient.client.block.CreeperBlockEntityRenderer;
import org.land.skycraftclient.client.packet.AnimationPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import org.land.skycraftclient.client.packet.AnimationPayload;
import org.land.skycraftclient.register.ModBlockEntities;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

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
        registerRenderers();
        BlockEntityRendererFactories.register(ModBlockEntities.CREEPER_BLOCK_ENTITY, CreeperBlockEntityRenderer::new);

    }
    private void registerRenderers() {
    }

}
