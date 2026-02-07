package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;

/**
 * Defines a handler capable of accepting (filling) or providing (draining) Flux resources.
 * <p>
 * This interface is typically implemented by machines, pipes, or storage blocks.
 * </p>
 *
 * @param <T> The carrier type (e.g., LiquidFlux).
 * @param <D> The data type (e.g., LiquidStack).
 */
public interface IFluxHandler<T extends IFlux<T, D>, D> {

    /**
     * Attempts to insert resources into this handler.
     *
     * @param resource The resource carrier to insert.
     * @param action   If {@code SIMULATE}, the insertion is only calculated but not performed.
     * @return The <b>remainder</b> of the resource that could NOT be inserted.
     * Returns an empty Flux if all resources were accepted.
     */
    @Nonnull
    T fill(T resource, FluxAction action);

    /**
     * Attempts to extract resources from this handler based on a request template.
     * <p>
     * The {@code maxDrainResource} acts as a filter/order form:
     * <ul>
     * <li>If it contains specific data, only matching resources are drained.</li>
     * <li>If it contains a wildcard (e.g., empty liquid), it performs a "blind" drain up to the requested quantity.</li>
     * </ul>
     * </p>
     *
     * @param maxDrainResource A carrier defining the type (intent) and maximum amount to drain.
     * @param action           If {@code SIMULATE}, the extraction is only calculated.
     * @return A carrier containing the resources that were <b>actually extracted</b>.
     * Returns an empty Flux if nothing could be drained.
     */
    @Nonnull
    T drain(T maxDrainResource, FluxAction action);

    /**
     * Defines the mode of operation for Flux interactions.
     */
    enum FluxAction {
        /**
         * Perform the action and modify the state (actually move resources).
         */
        EXECUTE,
        /**
         * Calculate the result without modifying any state (prediction/checking).
         */
        SIMULATE;

        public boolean execute() {
            return this == EXECUTE;
        }

        public boolean simulate() {
            return this == SIMULATE;
        }
    }
}
