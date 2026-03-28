package com.benchenssever.fluxvault.api;

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
     * Inserts resources into this handler.
     * <p>
     * <b>MUTABILITY CONTRACT:</b>
     * If the action is {@link FluxAction#EXECUTE} or {@link FluxAction#EXECUTE_EXACT}, the provided {@code resource} MAY be mutated
     * (e.g., quantities reduced) to reflect the remaining unaccepted payload for subsequent routing.
     * If the action is {@link FluxAction#SIMULATE} or {@link FluxAction#SIMULATE_EXACT}, the {@code resource} MUST remain completely unmodified.
     * </p>
     *
     * @param resource The resources to insert.
     * @param action   The operation mode (SIMULATE, EXECUTE, or their EXACT variants).
     * @return The resources <b>actually inserted</b>. Returns empty if nothing was filled.
     */
    @Nonnull
    F fill(@Nonnull F resource, @Nonnull FluxAction action);

    /**
     * Extracts resources from this handler based on a request.
     * <p>
     * <b>MUTABILITY CONTRACT:</b>
     * If the action is {@link FluxAction#EXECUTE} or {@link FluxAction#EXECUTE_EXACT}, the provided {@code requestResources} MAY be mutated
     * (e.g., target definitions altered, quantities reduced) to reflect the remaining unfulfilled request.
     * If the action is {@link FluxAction#SIMULATE} or {@link FluxAction#SIMULATE_EXACT}, the {@code requestResources} MUST remain completely unmodified.
     * </p>
     *
     * @param requestResources A carrier defining the desired resources (filter) and maximum amounts to extract.
     * @param action           The operation mode (SIMULATE, EXECUTE, or their EXACT variants).
     * @return The resources <b>actually extracted</b>. Returns empty if nothing was drained.
     */
    @Nonnull
    F drain(@Nonnull F requestResources, @Nonnull FluxAction action);

    /**
     * Operation mode for Flux interactions.
     * Defines both the mutability intent (Execute vs Simulate) and the strictness of the transaction (Partial vs Exact).
     */
    enum FluxAction {
        /**
         * <b>Standard/Partial Execution:</b>
         * Modifies the state of both the handler and the provided resource payload.
         * Accepts or extracts as much of the requested amount as possible (Best-effort).
         */
        EXECUTE,

        /**
         * <b>Standard/Partial Simulation:</b>
         * Calculates the result without modifying the handler's state or the provided resource payload.
         * Evaluates how much of the requested amount *would* be processed.
         */
        SIMULATE,

        /**
         * <b>Strict/Exact Execution:</b>
         * Modifies states ONLY IF the exact requested amount can be accommodated.
         * Enforces an "All-or-Nothing" contract: if the container cannot fulfill the exact quantity requested,
         * no state changes occur and an empty result is returned.
         */
        EXECUTE_EXACT,

        /**
         * <b>Strict/Exact Simulation:</b>
         * Calculates the result for an exact amount transfer without modifying states.
         * Returns empty if the exact amount cannot be accommodated.
         */
        SIMULATE_EXACT;

        /**
         * Checks if this action permits state mutation.
         *
         * @return True if this is an execution action (EXECUTE or EXECUTE_EXACT).
         */
        public boolean execute() {
            return this == EXECUTE || this == EXECUTE_EXACT;
        }

        /**
         * Checks if this action strictly forbids state mutation.
         *
         * @return True if this is a simulation action (SIMULATE or SIMULATE_EXACT).
         */
        public boolean simulate() {
            return this == SIMULATE || this == SIMULATE_EXACT;
        }

        /**
         * Checks if this action enforces the rigid All-or-Nothing volume contract.
         *
         * @return True if this action requires the exact requested amount to be processed.
         */
        public boolean exact() {
            return this == EXECUTE_EXACT || this == SIMULATE_EXACT;
        }

        /**
         * Coerces the current operation intent into its strict (Exact) counterpart.
         * Preserves the existing mutability intent (Execute/Simulate).
         *
         * @return EXECUTE_EXACT if currently executing, SIMULATE_EXACT if currently simulating.
         */
        public FluxAction asExact() {
            return this.execute() ? EXECUTE_EXACT : SIMULATE_EXACT;
        }

        /**
         * Coerces the current operation intent into its lenient (Partial/Best-effort) counterpart.
         * Preserves the existing mutability intent (Execute/Simulate).
         *
         * @return EXECUTE if currently executing, SIMULATE if currently simulating.
         */
        public FluxAction asPartial() {
            return this.execute() ? EXECUTE : SIMULATE;
        }
    }
}
