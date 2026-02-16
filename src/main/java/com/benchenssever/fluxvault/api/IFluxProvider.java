package com.benchenssever.fluxvault.api;

import javax.annotation.Nullable;

/**
 * Interface for objects capable of providing specific Flux handlers.
 * <p>
 * Implemented by Blocks, Entities, or Components to expose their resource capabilities
 * (e.g., a machine that accepts both Liquid and Energy).
 * </p>
 */
public interface IFluxProvider {

    /**
     * Retrieves the handler for a specific Flux resource type.
     *
     * @param type The type of resource requested (e.g., {@link FluxType#LIQUID}).
     * @param <T>  The carrier type.
     * @param <D>  The data type.
     * @return The handler instance, or {@code null} if the type is not supported.
     */
    @Nullable
    <T extends IFlux<T, D>, D> IFluxHandler<T, D> getFluxHandler(FluxType<T, D> type);
}
