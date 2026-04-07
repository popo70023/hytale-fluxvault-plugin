/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FluxUtil {

    private FluxUtil() {
    }

    @Nullable
    public static Ref<ChunkStore> getBlockEntityRef(World world, Vector3i targetBlock) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());

        if (blockComponentChunk == null) return null;

        int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);

        if (blockRef != null && blockRef.isValid()) {
            BlockModule.BlockStateInfo stateInfo = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
            if (stateInfo != null && stateInfo.getChunkRef().isValid()) {
                return blockRef;
            }
        }
        return null;
    }

    @Nullable
    public static <T extends Component<ChunkStore>> T getBlockComponent(World world, Vector3i targetBlock, ComponentType<ChunkStore, T> componentType) {
        Ref<ChunkStore> blockRef = getBlockEntityRef(world, targetBlock);

        if (blockRef != null) {
            return blockRef.getStore().getComponent(blockRef, componentType);
        }

        return null;
    }

    public static void forEachBlockComponent(World world, Vector3i targetBlock, Consumer<Component<ChunkStore>> action) {
        Ref<ChunkStore> blockRef = getBlockEntityRef(world, targetBlock);
        if (blockRef == null) return;

        Store<ChunkStore> store = blockRef.getStore();
        Archetype<ChunkStore> archetype = store.getArchetype(blockRef);

        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<ChunkStore, ?> compType = archetype.get(i);
            if (compType != null) {
                action.accept(store.getComponent(blockRef, compType));
            }
        }
    }

    @Nullable
    public static <R> R queryBlockComponents(World world, Vector3i targetBlock, Function<Component<ChunkStore>, R> query) {
        Ref<ChunkStore> blockRef = getBlockEntityRef(world, targetBlock);
        if (blockRef == null) return null;

        Store<ChunkStore> store = blockRef.getStore();
        Archetype<ChunkStore> archetype = store.getArchetype(blockRef);

        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<ChunkStore, ?> compType = archetype.get(i);
            if (compType != null) {
                Component<ChunkStore> component = store.getComponent(blockRef, compType);

                R result = query.apply(component);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    @Nullable
    public static <F extends IFlux<D>, D> IFluxHandler<F> getBlockFluxHandler(World world, Vector3i targetBlock, FluxType<F, D> fluxType, @Nonnull BlockFace side, String slotName, @Nonnull IFluxHandlerProvider.FluxAccess access) {
        return queryBlockComponents(world, targetBlock, component -> {
            if (component instanceof IFluxHandlerProvider provider) {
                return provider.getFluxHandler(fluxType, side, slotName, access);
            }
            return null;
        });
    }

    @Nullable
    public static IFluxContainerProvider getBlockInformationProvider(World world, Vector3i targetBlock) {
        return queryBlockComponents(world, targetBlock, component -> {
            if (component instanceof IFluxContainerProvider provider) {
                return provider;
            }
            return null;
        });
    }

    public static <T extends Component<EntityStore>> T getEntityComponent(Ref<EntityStore> entityRef, ComponentType<EntityStore, T> componentType) {
        if (entityRef == null || !entityRef.isValid()) return null;
        Store<EntityStore> store = entityRef.getStore();
        return store.getComponent(entityRef, componentType);
    }

    public static void forEachEntityComponent(Ref<EntityStore> entityRef, Consumer<Component<EntityStore>> action) {
        if (entityRef == null || !entityRef.isValid()) return;

        Store<EntityStore> store = entityRef.getStore();
        Archetype<EntityStore> archetype = store.getArchetype(entityRef);

        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<EntityStore, ?> compType = archetype.get(i);
            if (compType != null) {
                action.accept(store.getComponent(entityRef, compType));
            }
        }
    }

    @Nullable
    public static <R> R queryEntityComponent(Ref<EntityStore> entityRef, Function<Component<EntityStore>, R> query) {
        if (entityRef == null || !entityRef.isValid()) return null;

        Store<EntityStore> store = entityRef.getStore();
        Archetype<EntityStore> archetype = store.getArchetype(entityRef);

        for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
            ComponentType<EntityStore, ?> compType = archetype.get(i);

            if (compType != null) {
                Component<EntityStore> component = store.getComponent(entityRef, compType);

                R result = query.apply(component);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    @Nullable
    public static <F extends IFlux<D>, D> IFluxHandler<F> getEntityFluxHandler(Ref<EntityStore> entityRef, @Nonnull FluxType<F, D> fluxType, @Nonnull String detail, @Nullable String slotName, @Nonnull IFluxHandlerProvider.FluxAccess access) {
        return queryEntityComponent(entityRef, component -> {
            if (component instanceof IFluxHandlerProvider provider) {
                return provider.getFluxHandler(fluxType, detail, slotName, access);
            }
            return null;
        });
    }
}
