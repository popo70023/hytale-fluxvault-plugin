/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The universal capability bridge for objects capable of participating in the Flux logistics network.
 * <p>
 * Implemented by Blocks, Entities, or Components to expose their routing and storage capabilities.
 * A single provider (e.g., an advanced machine) can multiplex multiple handlers (e.g., accepting both Liquid and Energy),
 * and can dynamically route capabilities based on the physical face being accessed.
 * </p>
 */
public interface IFluxHandlerProvider<R> {

    /**
     * Retrieves the authoritative handler for a specific Flux resource type, routed by the accessed block face.
     * <p>
     * <b>LIFECYCLE WARNING:</b> The returned handler represents the current logical state of the provider.
     * Callers should NEVER cache the returned {@code IFluxHandler} long-term across multiple ticks,
     * as the provider's capabilities may dynamically change (e.g., a machine component being uninstalled).
     * </p>
     *
     * @param type        The architectural type of the resource requested (e.g., {@link FluxType#LIQUID}).
     * @param accessPoint The physical or logical point from which the provider is being accessed
     *                    (e.g., a specific {@link BlockFace} for a block, or a bone name for an entity).
     *                    Can be {@code null} if requesting internal, dimensionless, or global access.
     * @param targetName  An optional identifier to directly target a specific internal sub-container
     *                    (e.g., {@code "Storage"} for a general inventory, or {@code "Fuel"} for a furnace).
     *                    Pass {@code null} or an empty string to rely on the provider's default routing logic
     *                    (which typically resolves the target based on the provided {@code accessPoint}).
     * @param access      The intended operation (FILL or DRAIN) the caller wishes to perform.
     *                    Used to evaluate physical compatibility before a connection is established
     *                    (e.g., an output-only face returning null for a FILL request to prevent pipe connection).
     *                    STRICTLY NON-NULL.
     * @param <F>         The carrier payload type.
     * @param <D>         The underlying data type.
     * @return The active handler instance ready for SIMULATE/EXECUTE operations, or {@code null} if the requested type/face is not supported.
     */
    @Nullable
    <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@Nonnull FluxType<F, D> type, @Nullable R accessPoint, @Nullable String targetName, @Nonnull FluxAccess access);

    /**
     * Defines the operational intent of a network connection or query.
     * <p>
     * Used by providers to determine if a physical or logical connection should be permitted based on the caller's objective.
     * </p>
     */
    enum FluxAccess {
        FILL,
        DRAIN;

        public boolean fill() {
            return this == FILL;
        }

        public boolean drain() {
            return this == DRAIN;
        }
    }

    interface Block extends IFluxHandlerProvider<BlockFace> {
    }

    interface Entity extends IFluxHandlerProvider<String> {
    }
}
