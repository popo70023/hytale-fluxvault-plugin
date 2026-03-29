package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.energy.container.SingleEnergyContainerComponent;
import io.github.popo70023.fluxvault.liquid.container.SingleLiquidContainerComponent;

public class ComponentTypes {
    public static ComponentType<ChunkStore, SingleLiquidContainerComponent> SINGLE_LIQUID_CONTAINER;
    public static ComponentType<ChunkStore, SingleEnergyContainerComponent> SINGLE_ENERGY_CONTAINER;

    public static void registerChunkStore(ComponentRegistryProxy<ChunkStore> Registry) {
        SINGLE_LIQUID_CONTAINER = Registry.registerComponent(SingleLiquidContainerComponent.class, SingleLiquidContainerComponent.ID, SingleLiquidContainerComponent.CODEC);
        SINGLE_ENERGY_CONTAINER = Registry.registerComponent(SingleEnergyContainerComponent.class, SingleEnergyContainerComponent.ID, SingleEnergyContainerComponent.CODEC);
    }
}
