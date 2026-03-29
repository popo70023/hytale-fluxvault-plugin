package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.protocol.BlockFace;

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
public interface IFluxProvider {

    /**
     * Retrieves the authoritative handler for a specific Flux resource type, routed by the accessed face.
     * <p>
     * <b>LIFECYCLE WARNING:</b> The returned handler represents the current logical state of the provider.
     * Callers should NEVER cache the returned {@code IFluxHandler} long-term across multiple ticks,
     * as the provider's capabilities may dynamically change (e.g., a machine component being uninstalled).
     * </p>
     *
     * @param type The architectural type of the resource requested (e.g., {@link FluxType#LIQUID}).
     * @param side The physical direction/face from which the provider is being accessed (e.g., {@link BlockFace#Up}).
     *             Must be {@link BlockFace#None} if the access is internal, dimensionless (e.g., an Item in inventory),
     *             or requesting the global/default handler. STRICTLY NON-NULL.
     * @param <F>  The carrier payload type.
     * @param <D>  The underlying data type.
     * @return The active handler instance ready for SIMULATE/EXECUTE operations, or {@code null} if the requested type/side is not supported.
     */
    @Nullable
    <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@Nonnull FluxType<F, D> type, @Nonnull BlockFace side);
}
