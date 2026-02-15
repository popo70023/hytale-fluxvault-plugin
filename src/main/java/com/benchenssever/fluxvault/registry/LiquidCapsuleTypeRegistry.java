package com.benchenssever.fluxvault.registry;

import com.benchenssever.fluxvault.liquid.LiquidCapsuleType;
import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LiquidCapsuleTypeRegistry implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, LiquidCapsuleTypeRegistry>> {
    public static final AssetBuilderCodec<String, LiquidCapsuleTypeRegistry> CODEC = AssetBuilderCodec.builder(LiquidCapsuleTypeRegistry.class, LiquidCapsuleTypeRegistry::new, Codec.STRING,
                    LiquidCapsuleTypeRegistry::setId, LiquidCapsuleTypeRegistry::getId, LiquidCapsuleTypeRegistry::setAssetData, LiquidCapsuleTypeRegistry::getAssetData)
            .append(new KeyedCodec<>("Data", new ArrayCodec<>(Data.CODEC, Data[]::new)), LiquidCapsuleTypeRegistry::setData, LiquidCapsuleTypeRegistry::getData).add()
            .build();

    private String registryId;
    private List<Data> data;
    private AssetExtraInfo.Data assetData;

    public static void registerLiquidCapsuleType() {
        Collection<LiquidCapsuleTypeRegistry> CapsuleType = FluxAssetRegistry.CAPSULE_TYPE_ASSET_STORE.getAssetMap().getAssetMap().values();
        for (LiquidCapsuleTypeRegistry capsuleType : CapsuleType) {
            for (Data data : capsuleType.getData()) {
                LiquidCapsuleType.registerItemToCapsule(data.capsuleId, data.capacity, data.itemId, data.liquidId);
            }
        }
    }

    @Override
    public String getId() {
        return registryId;
    }

    public void setId(String id) {
        this.registryId = id;
    }

    public Data[] getData() {
        return data == null ? new Data[0] : data.toArray(Data[]::new);
    }

    public void setData(Data[] data) {
        if (data == null) {
            this.data = Collections.emptyList();
        } else {
            this.data = List.of(data);
        }
    }

    public AssetExtraInfo.Data getAssetData() {
        return assetData;
    }

    public void setAssetData(AssetExtraInfo.Data assetData) {
        this.assetData = assetData;
    }

    public static class Data {
        public static final Codec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new)
                .append(new KeyedCodec<>("CapsuleId", Codec.STRING), (o, v) -> o.capsuleId = v, o -> o.capsuleId).add()
                .append(new KeyedCodec<>("Capacity", Codec.INTEGER), (o, v) -> o.capacity = v, o -> o.capacity).add()
                .append(new KeyedCodec<>("ItemId", Codec.STRING), (o, v) -> o.itemId = v, o -> o.itemId).add()
                .append(new KeyedCodec<>("LiquidId", Codec.STRING), (o, v) -> o.liquidId = v, o -> o.liquidId).add()
                .build();
        public String capsuleId;
        public int capacity;
        public String itemId;
        public String liquidId;
    }
}
