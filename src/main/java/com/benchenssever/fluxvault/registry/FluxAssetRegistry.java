package com.benchenssever.fluxvault.registry;

import com.benchenssever.fluxvault.liquid.Liquid;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.registry.AssetRegistry;

public class FluxAssetRegistry {
    public static HytaleAssetStore<String, Liquid, IndexedLookupTableAssetMap<String, Liquid>> LIQUID_ASSET_STORE = HytaleAssetStore.builder(Liquid.class, new IndexedLookupTableAssetMap<>(Liquid[]::new))
            .setPath("Item/Liquid")
            .setCodec(Liquid.CODEC)
            .setKeyFunction(Liquid::getId)
            .setReplaceOnRemove(_ -> Liquid.EMPTY)
            .build();

    public static HytaleAssetStore<String, LiquidCapsuleTypeRegistry, IndexedLookupTableAssetMap<String, LiquidCapsuleTypeRegistry>> CAPSULE_TYPE_ASSET_STORE = HytaleAssetStore.builder(LiquidCapsuleTypeRegistry.class, new IndexedLookupTableAssetMap<>(LiquidCapsuleTypeRegistry[]::new))
            .setPath("Item/Liquid/CapsuleType")
            .setCodec(LiquidCapsuleTypeRegistry.CODEC)
            .setKeyFunction(LiquidCapsuleTypeRegistry::getId)
            .setReplaceOnRemove(_ -> new LiquidCapsuleTypeRegistry())
            .build();

    public static void registerAssets(AssetRegistry assetRegistry) {
        assetRegistry.register(LIQUID_ASSET_STORE);
        assetRegistry.register(CAPSULE_TYPE_ASSET_STORE);
    }
}
