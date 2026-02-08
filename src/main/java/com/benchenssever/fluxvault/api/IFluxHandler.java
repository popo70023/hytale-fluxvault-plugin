package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;

/**
 * Standard interface for accepting (fill) and extracting (drain) Flux resources.
 * <p>
 * Implemented by any object that interacts with the logistics system, such as machines,
 * pipes, or storage containers.
 * </p>
 *
 * @param <T> The carrier type (e.g., LiquidFlux).
 * @param <D> The data type (e.g., LiquidStack).
 */
public interface IFluxHandler<T extends IFlux<T, D>, D> {

    /**
     * Inserts resources into this handler.
     *
     * @param resource The resources to insert.
     * @param action   The operation mode (SIMULATE or EXECUTE).
     * @return The <b>remainder</b> that could not be accepted. Returns empty if fully accepted.
     */
    @Nonnull
    T fill(T resource, FluxAction action);

    /**
     * Extracts resources from this handler based on a request.
     *
     * @param requestResources A carrier defining the desired resources (filter) and maximum amounts to extract.
     * @param action           The operation mode (SIMULATE or EXECUTE).
     * @return The resources <b>actually extracted</b>. Returns empty if nothing was drained.
     */
    @Nonnull
    T drain(T requestResources, FluxAction action);

    /**
     * Operation mode for Flux interactions.
     */
    enum FluxAction {
        /** Modifies the state (Standard operation). */
        EXECUTE,
        /** Calculates result without modifying state (Prediction/Check). */
        SIMULATE;

        /** @return True if this is an execution action. */
        public boolean execute() {
            return this == EXECUTE;
        }

        /** @return True if this is a simulation action. */
        public boolean simulate() {
            return this == SIMULATE;
        }
    }
}
