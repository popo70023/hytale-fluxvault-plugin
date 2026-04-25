/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.registry.AssetRegistry;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.common.interaction.InspectContainerInteraction;
import io.github.popo70023.fluxvault.common.system.FluxVaultBlockInitializationSystem;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.LiquidCapsuleType;
import io.github.popo70023.fluxvault.payload.liquid.interaction.DrainLiquidContainerBlockInteraction;
import io.github.popo70023.fluxvault.payload.liquid.interaction.FillLiquidContainerBlockInteraction;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;

public final class FluxAssetRegistry {

    private FluxAssetRegistry() {
    }

    public static HytaleAssetStore<String, Liquid, IndexedLookupTableAssetMap<String, Liquid>> LIQUID_ASSET_STORE = HytaleAssetStore.builder(Liquid.class, new IndexedLookupTableAssetMap<>(Liquid[]::new))
            .setPath("Item/Liquid/LiquidType")
            .setCodec(Liquid.CODEC)
            .setKeyFunction(Liquid::getId)
            .setReplaceOnRemove(_ -> Liquid.UNKNOWN)
            .build();

    public static HytaleAssetStore<String, LiquidCapsuleTypes, IndexedLookupTableAssetMap<String, LiquidCapsuleTypes>> CAPSULE_TYPE_ASSET_STORE = HytaleAssetStore.builder(LiquidCapsuleTypes.class, new IndexedLookupTableAssetMap<>(LiquidCapsuleTypes[]::new))
            .setPath("Item/Liquid/CapsuleType")
            .setCodec(LiquidCapsuleTypes.CODEC)
            .setKeyFunction(LiquidCapsuleTypes::getId)
            .setReplaceOnRemove(_ -> new LiquidCapsuleTypes())
            .build();

    public static HytaleAssetStore<String, FluxResource, IndexedLookupTableAssetMap<String, FluxResource>> FLUX_RESOURCE_ASSET_STORE = HytaleAssetStore.builder(FluxResource.class, new IndexedLookupTableAssetMap<>(FluxResource[]::new))
            .setPath("Item/FluxResource")
            .setCodec(FluxResource.CODEC)
            .setKeyFunction(FluxResource::getId)
            .setReplaceOnRemove(_ -> new FluxResource())
            .build();

    private static void registerAssets(AssetRegistry assetRegistry) {
        assetRegistry.register(LIQUID_ASSET_STORE);
        assetRegistry.register(CAPSULE_TYPE_ASSET_STORE);
        assetRegistry.register(FLUX_RESOURCE_ASSET_STORE);
    }

    private static void registerInteractionSystem(ComponentRegistryProxy<ChunkStore> chunkStoreRegistry) {
        chunkStoreRegistry.registerSystem(new FluxVaultBlockInitializationSystem());
    }

    private static void registerInteraction(CodecMapRegistry.Assets<Interaction, ?> codecRegistry) {
        codecRegistry.register(InspectContainerInteraction.Id, InspectContainerInteraction.class, InspectContainerInteraction.CODEC);
        codecRegistry.register(FillLiquidContainerBlockInteraction.Id, FillLiquidContainerBlockInteraction.class, FillLiquidContainerBlockInteraction.CODEC);
        codecRegistry.register(DrainLiquidContainerBlockInteraction.Id, DrainLiquidContainerBlockInteraction.class, DrainLiquidContainerBlockInteraction.CODEC);
        //codecRegistry.register(FillResourceContainerBlockInteraction.Id, FillResourceContainerBlockInteraction.class, FillResourceContainerBlockInteraction.CODEC);
    }

    public static void RegistryAtSetup(FluxVaultPlugin plugin) {
        ComponentTypes.registerChunkStore(plugin.getChunkStoreRegistry());
        ComponentTypes.registerEntityStore(plugin.getEntityStoreRegistry());
        FluxAssetRegistry.registerAssets(plugin.getAssetRegistry());
        FluxAssetRegistry.registerInteraction(plugin.getCodecRegistry(Interaction.CODEC));
    }

    public static void RegistryAtStart(FluxVaultPlugin plugin) {
        LiquidCapsuleTypes.registerLiquidCapsuleType();
        FluxType.registerFluxTypes();
        registerInteractionSystem(plugin.getChunkStoreRegistry());
    }

    public static void ClearCacheAtShutdown(FluxVaultPlugin plugin) {
        FluxType.clearCache();
        LiquidCapsuleType.clearCache();
    }
}
