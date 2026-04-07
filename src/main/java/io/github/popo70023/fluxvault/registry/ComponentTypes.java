/*
 * FluxVault - A universal transport protocol for Hytale.
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

public class ComponentTypes {
    public static ComponentType<ChunkStore, CreativeResourceBlock> CREATIVE_RESOURCE_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, SimpleResourceContainerBlock> SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, CreativeLiquidBlock> CREATIVE_LIQUID_BLOCK_COMPONENT;
    public static ComponentType<ChunkStore, SimpleLiquidContainerBlock> SIMPLE_LIQUID_CONTAINER_BLOCK_COMPONENT;

    public static void registerChunkStore(ComponentRegistryProxy<ChunkStore> Registry) {
        CREATIVE_RESOURCE_BLOCK_COMPONENT = Registry.registerComponent(CreativeResourceBlock.class, CreativeResourceBlock.Id, CreativeResourceBlock.CODEC);
        SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT = Registry.registerComponent(SimpleResourceContainerBlock.class, SimpleResourceContainerBlock.Id, SimpleResourceContainerBlock.CODEC);
        CREATIVE_LIQUID_BLOCK_COMPONENT = Registry.registerComponent(CreativeLiquidBlock.class, CreativeLiquidBlock.Id, CreativeLiquidBlock.CODEC);
        SIMPLE_LIQUID_CONTAINER_BLOCK_COMPONENT = Registry.registerComponent(SimpleLiquidContainerBlock.class, SimpleLiquidContainerBlock.Id, SimpleLiquidContainerBlock.CODEC);
    }

    public static void registerEntityStore(ComponentRegistryProxy<EntityStore> Registry) {
    }
}
