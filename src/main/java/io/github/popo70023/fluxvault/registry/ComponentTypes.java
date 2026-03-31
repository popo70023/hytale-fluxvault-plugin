/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.energy.component.CreativeEnergyComponent;
import io.github.popo70023.fluxvault.energy.component.SimpleEnergyContainerComponent;
import io.github.popo70023.fluxvault.liquid.component.CreativeLiquidComponent;
import io.github.popo70023.fluxvault.liquid.component.SimpleLiquidContainerComponent;

public class ComponentTypes {
    public static ComponentType<ChunkStore, CreativeEnergyComponent> CREATIVE_ENERGY_COMPONENT;
    public static ComponentType<ChunkStore, SimpleEnergyContainerComponent> SIMPLE_ENERGY_CONTAINER;
    public static ComponentType<ChunkStore, CreativeLiquidComponent> CREATIVE_LIQUID_COMPONENT;
    public static ComponentType<ChunkStore, SimpleLiquidContainerComponent> SIMPLE_LIQUID_CONTAINER;

    public static void registerChunkStore(ComponentRegistryProxy<ChunkStore> Registry) {
        CREATIVE_ENERGY_COMPONENT = Registry.registerComponent(CreativeEnergyComponent.class, CreativeEnergyComponent.ID, CreativeEnergyComponent.CODEC);
        SIMPLE_ENERGY_CONTAINER = Registry.registerComponent(SimpleEnergyContainerComponent.class, SimpleEnergyContainerComponent.ID, SimpleEnergyContainerComponent.CODEC);
        CREATIVE_LIQUID_COMPONENT = Registry.registerComponent(CreativeLiquidComponent.class, CreativeLiquidComponent.ID, CreativeLiquidComponent.CODEC);
        SIMPLE_LIQUID_CONTAINER = Registry.registerComponent(SimpleLiquidContainerComponent.class, SimpleLiquidContainerComponent.ID, SimpleLiquidContainerComponent.CODEC);
    }
}
