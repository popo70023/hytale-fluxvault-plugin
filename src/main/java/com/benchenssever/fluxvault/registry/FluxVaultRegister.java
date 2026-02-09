package com.benchenssever.fluxvault.registry;

import com.benchenssever.fluxvault.FluxVaultPlugin;
import com.benchenssever.fluxvault.liquid.container.SingleLiquidContainerRefSystem;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class FluxVaultRegister {

    public static void registerRefSystems(ComponentRegistryProxy<ChunkStore> chunkStoreRegistry) {
        FluxVaultPlugin.LOGGER.atInfo().log("Registering RefSystems for FluxVaultPlugin");
        chunkStoreRegistry.registerSystem(new SingleLiquidContainerRefSystem());
    }
}
