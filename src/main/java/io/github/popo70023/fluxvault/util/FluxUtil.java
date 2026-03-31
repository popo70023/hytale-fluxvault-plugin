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
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FluxUtil {

    private FluxUtil() {
    }

    @Nullable
    public static <T extends Component<ChunkStore>> T getBlockComponent(World world, Vector3i targetBlock, ComponentType<ChunkStore, T> componentType) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());

        if (blockComponentChunk != null) {
            int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);

            if (blockRef != null && blockRef.isValid()) {
                BlockModule.BlockStateInfo stateInfo = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

                if (stateInfo != null && stateInfo.getChunkRef().isValid()) {
                    return chunkStore.getStore().getComponent(blockRef, componentType);
                }
            }
        }
        return null;
    }

    @Nullable
    public static <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(World world, Vector3i targetBlock, FluxType<F, D> fluxType, @Nonnull BlockFace side, @Nonnull IFluxProvider.FluxAccess access) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());

        if (blockComponentChunk != null) {
            int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);

            if (blockRef != null && blockRef.isValid()) {
                BlockModule.BlockStateInfo stateInfo = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

                if (stateInfo == null || !stateInfo.getChunkRef().isValid()) {
                    return null;
                }

                Store<ChunkStore> store = chunkStore.getStore();
                Archetype<ChunkStore> archetype = store.getArchetype(blockRef);
                for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
                    ComponentType<ChunkStore, ?> compType = archetype.get(i);

                    if (compType != null) {
                        Component<ChunkStore> component = store.getComponent(blockRef, compType);

                        if (component instanceof IFluxProvider provider) {
                            IFluxHandler<F> handler = provider.getFluxHandler(fluxType, side, access);
                            if (handler != null) {
                                return handler;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
