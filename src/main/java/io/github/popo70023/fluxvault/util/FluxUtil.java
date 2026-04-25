/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FluxUtil {

    private FluxUtil() {
    }

    public static void forEachBlockComponent(World world, Vector3i targetBlock, Consumer<Component<ChunkStore>> action) {
        Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(world, targetBlock.x, targetBlock.y, targetBlock.z);
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
        Ref<ChunkStore> blockRef = BlockModule.getBlockEntity(world, targetBlock.x, targetBlock.y, targetBlock.z);
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
    public static <F extends IFlux<D>, D> IFluxHandler<F> getBlockFluxHandler(World world, Vector3i targetBlock, FluxType<F, D> fluxType, @Nullable BlockFace side, String targetName, @Nonnull IFluxHandlerProvider.FluxAccess access) {
        return queryBlockComponents(world, targetBlock, component -> {
            if (component instanceof IFluxHandlerProvider.Block provider) {
                return provider.getFluxHandler(fluxType, side, targetName, access);
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
    public static <F extends IFlux<D>, D> IFluxHandler<F> getEntityFluxHandler(Ref<EntityStore> entityRef, @Nonnull FluxType<F, D> fluxType, @Nonnull String detail, @Nullable String targetName, @Nonnull IFluxHandlerProvider.FluxAccess access) {
        return queryEntityComponent(entityRef, component -> {
            if (component instanceof IFluxHandlerProvider.Entity provider) {
                return provider.getFluxHandler(fluxType, detail, targetName, access);
            }
            return null;
        });
    }
}
