/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import javax.annotation.Nonnull;

/**
 * Standard interface for accepting (fill) and extracting (drain) Flux resources.
 * <p>
 * Implemented by any object that interacts with the logistics system, such as machines,
 * pipes, or storage containers.
 * </p>
 *
 * @param <F> The carrier type (e.g., LiquidFlux).
 */
public interface IFluxHandler<F extends IFlux<?>> {

    /**
     * Determines whether this handler is capable of processing the specified {@link FluxType}.
     * <p>
     * This method delegates the capability routing to the handler itself, enabling high compatibility
     * and dynamic multiplexing. Implementations should evaluate whether the incoming type's carrier class
     * (via {@code fluxType.getResourceClass()}) is assignable to the specific payload class ({@code F})
     * this handler manages, rather than strictly equating the FluxType instances.
     * </p>
     *
     * @param fluxType The type definition querying for physical and logical compatibility.
     * @return {@code true} if this handler can safely process the carrier payload associated with the given type; {@code false} otherwise.
     */
    boolean matchesFluxType(FluxType<?, ?> fluxType);

    /**
     * Inserts resources into this handler.
     * <p>
     * <b>CARRIER CONTRACT:</b>
     * The handler MUST respect all attributes defined by the provided {@code resource}:
     * <ul>
     *   <li>{@link IFlux#isExact()}: If true, the operation must be All-or-Nothing.</li>
     *   <li>{@link IFlux#getTransferLimit()}: The inserted amount must not exceed this limit.</li>
     *   <li>{@link IFlux#getValidator()}: Only payloads satisfying this predicate are allowed.</li>
     * </ul>
     * </p>
     * <p>
     * <b>MUTABILITY CONTRACT:</b>
     * If the action is {@link FluxAction#EXECUTE}, the provided {@code resource} MAY be mutated
     * (e.g., quantities reduced) to reflect the remaining unaccepted payload for subsequent routing.
     * If the action is {@link FluxAction#SIMULATE}, the {@code resource} MUST remain completely unmodified.
     * </p>
     *
     * @param resource The resources to insert.
     * @param action   The operation mode (SIMULATE or EXECUTE).
     * @return The resources <b>actually inserted</b>. Returns empty if nothing was filled or if any carrier contract failed.
     */
    @Nonnull
    F fill(@Nonnull F resource, @Nonnull FluxAction action);

    /**
     * Extracts resources from this handler based on a request.
     * <p>
     * <b>CARRIER CONTRACT:</b>
     * The handler MUST respect all attributes defined by the {@code requestResources}:
     * <ul>
     *   <li>{@link IFlux#isExact()}: If true, the operation must be All-or-Nothing.</li>
     *   <li>{@link IFlux#getTransferLimit()}: The extracted amount must not exceed this limit.</li>
     *   <li>{@link IFlux#getValidator()}: Only payloads satisfying this predicate can be extracted.</li>
     * </ul>
     * </p>
     * <p>
     * <b>MUTABILITY CONTRACT:</b>
     * If the action is {@link FluxAction#EXECUTE}, the provided {@code requestResources} MAY be mutated
     * (e.g., quantities reduced) to reflect the remaining unfulfilled request.
     * If the action is {@link FluxAction#SIMULATE}, the {@code requestResources} MUST remain completely unmodified.
     * </p>
     *
     * @param requestResources A carrier defining the desired resources and limits to extract.
     * @param action           The operation mode (SIMULATE or EXECUTE).
     * @return The resources <b>actually extracted</b>. Returns empty if nothing was drained or if any carrier contract failed.
     */
    @Nonnull
    F drain(@Nonnull F requestResources, @Nonnull FluxAction action);

    /**
     * Operation mode for Flux interactions.
     * Defines the mutability intent (Execute vs Simulate).
     */
    enum FluxAction {
        /**
         * <b>Execution Mode:</b>
         * Permitted to modify the state of both the handler and the provided resource payload.
         */
        EXECUTE,

        /**
         * <b>Simulation Mode:</b>
         * Calculates the result without modifying any logical or physical states.
         */
        SIMULATE;

        /**
         * @return True if this is an execution action.
         */
        public boolean execute() {
            return this == EXECUTE;
        }

        /**
         * @return True if this is a simulation action.
         */
        public boolean simulate() {
            return this == SIMULATE;
        }
    }
}
