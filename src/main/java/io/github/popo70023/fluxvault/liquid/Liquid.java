package io.github.popo70023.fluxvault.liquid;

import io.github.popo70023.fluxvault.registry.FluxAssetRegistry;
import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemTranslationProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Liquid implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, Liquid>> {

    public static final String EMPTY_ID = "Empty";
    public static final String UNKNOWN_ID = "Unknown";
    public static final AssetBuilderCodec<String, Liquid> CODEC = AssetBuilderCodec.builder(Liquid.class, Liquid::new, Codec.STRING,
                    (o, v) -> o.liquidID = v, Liquid::getId, (o, v) -> o.assetData = v, Liquid::getAssetData)
            .appendInherited(new KeyedCodec<>("TranslationProperties", ItemTranslationProperties.CODEC), (o, v) -> o.translationProperties = v, Liquid::getTranslationProperties, (o, p) -> o.translationProperties = p.translationProperties)
            .documentation("The translation properties for this liquid asset.").add()
            .appendInherited(new KeyedCodec<>("Hazards", Codec.STRING_ARRAY), Liquid::setTraitsByArrays, Liquid::getHazardsToArrays, (o, p) -> o.hazards = p.hazards)
            .documentation("A list of hazard traits (e.g., Molten, Corrosive) associated with this liquid.").add()
            .build();
    public static final Liquid EMPTY = new Liquid(EMPTY_ID, new ItemTranslationProperties("server.items.Fluid_Empty.name", "server.items.Fluid_Empty.description"), new String[0]);
    public static final Liquid UNKNOWN = new Liquid();

    protected String liquidID;
    protected ItemTranslationProperties translationProperties;
    protected Set<String> hazards;
    protected AssetExtraInfo.Data assetData;

    public Liquid() {
        this.liquidID = UNKNOWN_ID;
        this.translationProperties = new ItemTranslationProperties("server.items.Fluid_Unknown.name", "server.items.Fluid_Unknown.description");
        this.hazards = Set.of();
    }

    public Liquid(String liquidID, ItemTranslationProperties translationProperties, String[] tags) {
        this.liquidID = liquidID;
        this.translationProperties = translationProperties;
        this.hazards = Set.of(tags);
    }

    public static Liquid getLiquidById(String id) {
        return FluxAssetRegistry.LIQUID_ASSET_STORE.getAssetMap().getAsset(id);
    }

    @Override
    public String getId() {
        return liquidID;
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
}
