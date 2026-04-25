/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.payload.liquid.component.CreativeLiquidBlock;
import io.github.popo70023.fluxvault.payload.liquid.component.SimpleLiquidContainerBlock;
import io.github.popo70023.fluxvault.payload.resource.component.CreativeResourceBlock;
import io.github.popo70023.fluxvault.payload.resource.component.SimpleResourceContainerBlock;

public final class ComponentTypes {

    private ComponentTypes() {
    }

    public static ComponentType<ChunkStore, CreativeLiquidBlock> CREATIVE_LIQUID_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, SimpleLiquidContainerBlock> SIMPLE_LIQUID_CONTAINER_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, CreativeResourceBlock> CREATIVE_RESOURCE_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, SimpleResourceContainerBlock> SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT;

    public static void registerChunkStore(ComponentRegistryProxy<ChunkStore> registry) {
        CREATIVE_LIQUID_BLOCK_COMPONENT = registry.registerComponent(CreativeLiquidBlock.class, CreativeLiquidBlock.Id, CreativeLiquidBlock.CODEC);
        SIMPLE_LIQUID_CONTAINER_BLOCK_COMPONENT = FluxVaultSystemRegistry.registerVaultComponent(registry.registerComponent(SimpleLiquidContainerBlock.class, SimpleLiquidContainerBlock.Id, SimpleLiquidContainerBlock.CODEC));
        CREATIVE_RESOURCE_BLOCK_COMPONENT = registry.registerComponent(CreativeResourceBlock.class, CreativeResourceBlock.Id, CreativeResourceBlock.CODEC);
        SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT = FluxVaultSystemRegistry.registerVaultComponent(registry.registerComponent(SimpleResourceContainerBlock.class, SimpleResourceContainerBlock.Id, SimpleResourceContainerBlock.CODEC));
    }

    public static void registerEntityStore(ComponentRegistryProxy<EntityStore> registry) {
    }
}
