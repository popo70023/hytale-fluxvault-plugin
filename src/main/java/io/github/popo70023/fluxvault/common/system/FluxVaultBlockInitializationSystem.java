/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.system;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.api.IFluxVaultHost;
import io.github.popo70023.fluxvault.registry.FluxVaultSystemRegistry;
import io.github.popo70023.fluxvault.util.BlockFaceUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashSet;
import java.util.Set;

public class FluxVaultBlockInitializationSystem extends RefSystem<ChunkStore> {
    private final Set<ComponentType<ChunkStore, ? extends Component<ChunkStore>>> registeredTypes;
    private final Query<ChunkStore> query;

    public FluxVaultBlockInitializationSystem() {
        this.registeredTypes = FluxVaultSystemRegistry.getRegisteredComponent();
        if (registeredTypes.isEmpty()) {
            throw new IllegalStateException("No FluxVault machines registered before system initialization!");
        }

        @SuppressWarnings("unchecked")
        ComponentType<ChunkStore, ? extends Component<ChunkStore>>[] typeArray = registeredTypes.toArray(ComponentType[]::new);

        this.query = Query.and(
                Query.or(typeArray),
                BlockModule.get().getBlockStateInfoComponentType()
        );
    }

    @Override
    public void onEntityAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.get().getBlockStateInfoComponentType());
        if (info == null) return;

        Set<Component<ChunkStore>> targetComponent = new HashSet<>();
        for (ComponentType<ChunkStore, ? extends Component<ChunkStore>> type : registeredTypes) {
            Component<ChunkStore> findComponent = commandBuffer.getComponent(ref, type);
            if (findComponent != null) {
                targetComponent.add(findComponent);
            }
        }

        for (Component<ChunkStore> component : targetComponent) {
            if (component instanceof IFluxVaultHost hostComponent) {
                World world = store.getExternalData().getWorld();
                hostComponent.registerChangeEvent(_ -> {
                    if (world.isInThread()) {
                        info.markNeedsSaving();
                    } else {
                        world.execute(info::markNeedsSaving);
                    }
                });
            }

            if (component instanceof IFluxVaultHost.Block blockComponent) {
                RotationTuple rotationTuple = BlockFaceUtil.getRotationTuple(ref);
                if (rotationTuple != null) {
                    blockComponent.updateRotation(rotationTuple.yaw());
                }
            }
        }
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

    }

    @NullableDecl
    @Override
    public Query<ChunkStore> getQuery() {
        return this.query;
    }
}
