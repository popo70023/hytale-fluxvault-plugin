package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.registry.ComponentTypes;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static com.benchenssever.fluxvault.FluxVaultPlugin.LOGGER;

public class SingleLiquidContainerRefSystem extends RefSystem<ChunkStore> {
    @Override
    public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        SingleLiquidContainerComponent LiquidContainerComponent = commandBuffer.getComponent(ref, ComponentTypes.SINGLE_LIQUID_CONTAINER);
        if (LiquidContainerComponent == null) {
            LOGGER.atInfo().log("LiquidContainerComponent is null for ref " + ref);
        }
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return Query.and(
                BlockModule.BlockStateInfo.getComponentType(),
                ComponentTypes.SINGLE_LIQUID_CONTAINER
        );
    }
}
