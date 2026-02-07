package com.benchenssever.fluxvault.api;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Interface for objects (like Blocks, Entities, or Parts) that can provide Flux Handlers.
 * <p>
 * This capability provider pattern allows an object to support multiple types of resources
 * (e.g., a machine holding both Liquid and Energy) and expose the appropriate handler dynamically.
 * </p>
 */
public interface IFluxProvider {

    /**
     * Retrieves a handler for the specified Flux type.
     *
     * @param type The type of handler requested (e.g., {@link FluxType#LIQUID}).
     * @param <T>  The carrier type.
     * @param <D>  The data type.
     * @return A handler instance for the requested type, or {@code null} if this provider does not support the given type.
     */
    @NullableDecl
    <T extends IFlux<T, D>, D> IFluxHandler<T, D> getFluxHandler(FluxType<T, D> type);
}
