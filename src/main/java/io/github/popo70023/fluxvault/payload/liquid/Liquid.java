/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTranslationProperties;
import io.github.popo70023.fluxvault.registry.FluxAssetRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Liquid implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, Liquid>> {

    public static final String EMPTY_ID = "Empty";
    public static final String UNKNOWN_ID = "Unknown";
    public static final String LIQUID_ID_KEY = "LiquidId";
    public static final AssetBuilderCodec<String, Liquid> CODEC;
    public static final Liquid UNKNOWN;
    public static final Liquid EMPTY;

    protected String liquidId;
    protected ItemTranslationProperties translationProperties;
    protected Set<String> hazards;
    protected AssetExtraInfo.Data assetData;

    public Liquid() {
        this.liquidId = UNKNOWN_ID;
        this.translationProperties = new ItemTranslationProperties("server.items.Fluid_Unknown.name", "server.items.Fluid_Unknown.description");
        this.hazards = Set.of();
    }

    public Liquid(String liquidId, ItemTranslationProperties translationProperties, String[] tags) {
        this.liquidId = liquidId;
        this.translationProperties = translationProperties;
        this.hazards = Set.of(tags);
    }

    public static Liquid getLiquidById(String id) {
        return FluxAssetRegistry.LIQUID_ASSET_STORE.getAssetMap().getAsset(id);
    }

    @Override
    public String getId() {
        return liquidId;
    }

    public ItemTranslationProperties getTranslationProperties() {
        return this.translationProperties;
    }

    public String[] getHazardsToArrays() {
        return hazards.toArray(String[]::new);
    }

    public Set<String> getHazards() {
        return hazards;
    }

    private void setTraitsByArrays(String[] tags) {
        if (tags == null) {
            this.hazards = Collections.emptySet();
            return;
        }
        this.hazards = Set.copyOf(new HashSet<>(Arrays.asList(tags)));
    }

    public AssetExtraInfo.Data getAssetData() {
        return assetData;
    }

    static {
        CODEC = AssetBuilderCodec.builder(Liquid.class, Liquid::new, Codec.STRING,
                        (o, v) -> o.liquidId = v, Liquid::getId, (o, v) -> o.assetData = v, Liquid::getAssetData)
                .appendInherited(new KeyedCodec<>("TranslationProperties", ItemTranslationProperties.CODEC), (o, v) -> o.translationProperties = v, Liquid::getTranslationProperties, (o, p) -> o.translationProperties = p.translationProperties)
                .documentation("The translation properties for this liquid asset.").add()
                .appendInherited(new KeyedCodec<>("Hazards", Codec.STRING_ARRAY), Liquid::setTraitsByArrays, Liquid::getHazardsToArrays, (o, p) -> o.hazards = p.hazards)
                .documentation("A list of hazard traits (e.g., Molten, Corrosive) associated with this liquid.").add()
                .build();
        UNKNOWN = new Liquid();
        EMPTY = new Liquid(EMPTY_ID, new ItemTranslationProperties("server.items.Fluid_Empty.name", "server.items.Fluid_Empty.description"), new String[0]);
    }
}
