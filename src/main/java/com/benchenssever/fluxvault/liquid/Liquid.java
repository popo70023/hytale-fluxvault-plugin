package com.benchenssever.fluxvault.liquid;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//TODO: 這個類別的主要目標是定義一種液體類型，包含一個唯一的識別符（liquidID）和一組相關的標籤（hazards）。核心邏輯會在構造函數裡實作，確保它能正確初始化 liquidID 和 hazards，並提供必要的方法來訪問這些屬性。
public class Liquid implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, Liquid>> {

    public static final String EMPTY_ID = "empty";
    public static final AssetBuilderCodec<String, Liquid> CODEC = AssetBuilderCodec.builder(Liquid.class, Liquid::new, Codec.STRING,
                    Liquid::setId, Liquid::getId, Liquid::setAssetData, Liquid::getAssetData)
            .append(new KeyedCodec<>("Hazards", Codec.STRING_ARRAY), Liquid::setTraitsByArrays, Liquid::getHazardsToArrays).add()
            .build();
    public static final Liquid EMPTY = new Liquid();
    public static IndexedLookupTableAssetMap<String, Liquid> LIQUIDS;

    private String liquidID;
    private Set<String> hazards;
    private AssetExtraInfo.Data assetData;

    public Liquid() {
        this.liquidID = EMPTY_ID;
        this.hazards = Set.of();
    }

    public Liquid(String liquidID, String[] tags) {
        this.liquidID = liquidID;
        this.hazards = Set.of(tags);
    }

    @Override
    public String getId() {
        return liquidID;
    }

    private void setId(String liquidID) {
        this.liquidID = liquidID;
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

    private void setAssetData(AssetExtraInfo.Data assetData) {
        this.assetData = assetData;
    }
}
