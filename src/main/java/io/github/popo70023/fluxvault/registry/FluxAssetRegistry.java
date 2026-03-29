package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.registry.AssetRegistry;
import com.hypixel.hytale.server.core.plugin.registry.CodecMapRegistry;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.liquid.Liquid;
import io.github.popo70023.fluxvault.liquid.interaction.DrainLiquidContainerInteraction;
import io.github.popo70023.fluxvault.liquid.interaction.FillLiquidContainerInteraction;
import io.github.popo70023.fluxvault.liquid.interaction.OpenSingleLiquidContainerInteraction;
import io.github.popo70023.fluxvault.liquid.interaction.SingleLiquidContainerInformationInteraction;

public class FluxAssetRegistry {
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

    private static void registerAssets(AssetRegistry assetRegistry) {
        assetRegistry.register(LIQUID_ASSET_STORE);
        assetRegistry.register(CAPSULE_TYPE_ASSET_STORE);
    }

    private static void registerInteraction(CodecMapRegistry.Assets<Interaction, ?> codecRegistry) {
        codecRegistry.register(FillLiquidContainerInteraction.ID, FillLiquidContainerInteraction.class, FillLiquidContainerInteraction.CODEC);
        codecRegistry.register(DrainLiquidContainerInteraction.ID, DrainLiquidContainerInteraction.class, DrainLiquidContainerInteraction.CODEC);
        codecRegistry.register(OpenSingleLiquidContainerInteraction.ID, OpenSingleLiquidContainerInteraction.class, OpenSingleLiquidContainerInteraction.CODEC);
        codecRegistry.register(SingleLiquidContainerInformationInteraction.ID, SingleLiquidContainerInformationInteraction.class, SingleLiquidContainerInformationInteraction.CODEC);
    }

    public static void RegistryAtSetup(FluxVaultPlugin plugin) {
        ComponentTypes.registerChunkStore(plugin.getChunkStoreRegistry());
        FluxAssetRegistry.registerAssets(plugin.getAssetRegistry());
        FluxAssetRegistry.registerInteraction(plugin.getCodecRegistry(Interaction.CODEC));
    }

    public static void RegistryAtStart(FluxVaultPlugin plugin) {
        LiquidCapsuleTypes.registerLiquidCapsuleType();
    }
}
