/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTranslationProperties;
import io.github.popo70023.fluxvault.registry.FluxAssetRegistry;

public class FluxResource implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, FluxResource>> {
    public static final AssetBuilderCodec<String, FluxResource> CODEC;
    public static final String RESOURCE_ID_KEY = "ResourceID";
    public static final String UNKNOWN_ID = "Unknown_Resource";

    private String resourceId;
    private String resourceUnit;
    protected ItemTranslationProperties translationProperties;
    protected AssetExtraInfo.Data assetData;

    public FluxResource() {
        this.resourceId = UNKNOWN_ID;
        this.translationProperties = new ItemTranslationProperties("server.fluxvault.Resource.Unknown.name", "server.fluxvault.Resource.Unknown.description");
    }

    public static FluxResource getResourceById(String id) {
        return FluxAssetRegistry.FLUX_RESOURCE_ASSET_STORE.getAssetMap().getAsset(id);
    }

    @Override
    public String getId() {
        return resourceId;
    }

    public String getResourceUnit() {
        return resourceUnit;
    }

    public ItemTranslationProperties getTranslationProperties() {
        return translationProperties;
    }

    public AssetExtraInfo.Data getAssetData() {
        return assetData;
    }

    static {
        CODEC = AssetBuilderCodec.builder(FluxResource.class, FluxResource::new, Codec.STRING, (o, v) -> o.resourceId = v, FluxResource::getId, (o, v) -> o.assetData = v, FluxResource::getAssetData)
                .append(new KeyedCodec<>("TranslationProperties", ItemTranslationProperties.CODEC), (o, v) -> o.translationProperties = v, FluxResource::getTranslationProperties)
                .documentation("The translation properties for this resource asset.").add()
                .append(new KeyedCodec<>("ResourceUnit", Codec.STRING), (o, v) -> o.resourceUnit = v, FluxResource::getResourceUnit)
                .documentation("The display unit symbol for this resource (e.g., FE, mB).").add()
                .build();
    }
}
