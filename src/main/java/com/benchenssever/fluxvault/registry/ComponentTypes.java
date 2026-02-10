package com.benchenssever.fluxvault.registry;

import com.benchenssever.fluxvault.energy.container.SingleEnergyContainerComponent;
import com.benchenssever.fluxvault.liquid.container.SingleLiquidContainerComponent;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class ComponentTypes {
    public static ComponentType<ChunkStore, SingleLiquidContainerComponent> SINGLE_LIQUID_CONTAINER;
    public static ComponentType<ChunkStore, SingleEnergyContainerComponent> SINGLE_ENERGY_CONTAINER;

    public static void registerChunkStore(ComponentRegistryProxy<ChunkStore> Registry) {
        SINGLE_LIQUID_CONTAINER = Registry.registerComponent(SingleLiquidContainerComponent.class, "SingleLiquidContainerComponent", SingleLiquidContainerComponent.CODEC);
        SINGLE_ENERGY_CONTAINER = Registry.registerComponent(SingleEnergyContainerComponent.class, "SingleEnergyContainerComponent", SingleEnergyContainerComponent.CODEC);
    }
}
