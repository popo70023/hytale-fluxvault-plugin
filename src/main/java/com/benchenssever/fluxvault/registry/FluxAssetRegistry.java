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

    public static void registerAssets(AssetRegistry assetRegistry) {
        assetRegistry.register(LIQUID_ASSET_STORE);
    }

    public static void registerMap() {
        Liquid.LIQUIDS = LIQUID_ASSET_STORE.getAssetMap();
    }
}
