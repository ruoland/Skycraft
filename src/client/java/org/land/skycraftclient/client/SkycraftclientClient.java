package org.land.skycraftclient.client;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlowLichenBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.land.skycraftclient.client.packet.AnimationPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import org.land.skycraftclient.client.packet.AnimationPayload;

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

        Blocks.GLOW_LICHEN.getSettings().luminance(GlowLichenBlock.getLuminanceSupplier(0)).emissiveLighting(((state, world, pos) -> false));

        removeBlock(Blocks.GLOW_LICHEN.getDefaultState());
    }

    public void removeBlock(BlockState block){
        String id = block.getRegistryEntry().getIdAsString().split(":")[1];
        System.out.println(block.getRegistryEntry().getIdAsString() +" - "+id);

        BiomeModifications.create(Identifier.of("skycraftclient", "remove_"+id))
                .add(ModificationPhase.REMOVALS, BiomeSelectors.all(), context -> {
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.UNDERGROUND_DECORATION,
                            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("minecraft", id)));
                });
        BiomeModifications.create(Identifier.of("skycraftclient", "remove_glow_lichen"))
                .add(ModificationPhase.REMOVALS, BiomeSelectors.all(), context -> {
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.RAW_GENERATION,
                            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("minecraft", id)));
                });

        BiomeModifications.create(Identifier.of("skycraftclient", "remove_glow_lichen"))
                .add(ModificationPhase.REMOVALS, BiomeSelectors.all(), context -> {
                    context.getGenerationSettings().removeFeature(GenerationStep.Feature.VEGETAL_DECORATION,
                            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of("minecraft", id)));
                });
    }
}
