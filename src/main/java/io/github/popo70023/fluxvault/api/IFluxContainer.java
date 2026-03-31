/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import java.util.List;
import java.util.function.Predicate;

/**
 * A container that exposes the internal storage state for inspection and direct manipulation.
 * <p>
 * <b>DANGER:</b> Unlike {@link IFluxHandler}, methods in this interface bypass logistics contracts
 * (such as SIMULATE/EXECUTE and transfer limits). This interface provides raw state access.
 * It is intended ONLY for specific architectural layers such as GUI rendering,
 * Component state persistence (Serialization/Deserialization), or absolute administrative overrides.
 * </p>
 *
 * @param <D> The data type (e.g., LiquidStack, FluxEnergy).
 */
public interface IFluxContainer<D> {

    /**
     * @return The maximum number of distinct stacks (slots) this container can physically hold.
     */
    int getContainerMaxSize();

    /**
     * Retrieves a snapshot or view of all contents.
     * <p>
     * <b>MUTABILITY WARNING:</b> The returned list and its elements MUST be treated as strictly read-only.
     * Mutating these objects directly will bypass the container's concurrency locks and trigger thread-safety
     * violations or dirty reads.
     * </p>
     *
     * @return A list of the current contents. Never null, but may contain empty equivalents.
     */
    List<D> getContents();

    /**
     * Retrieves the exact content residing in a specific slot.
     * <p>
     * <b>MUTABILITY WARNING:</b> Treat the returned object as strictly read-only.
     * </p>
     *
     * @param index The zero-based index of the slot.
     * @return The content in the slot, or the system's empty equivalent.
     */
    D getContent(int index);

    /**
     * Directly overwrites the content in a slot, bypassing all standard I/O capability checks.
     * <p>
     * <b>NULL CONTRACT:</b> Passing {@code null} as the content MUST be interpreted as a valid
     * command to CLEAR the slot. Implementations MUST translate {@code null} into the system's
     * standard Empty object (e.g., {@code FluxEnergy.of(0)} or {@code LiquidStack.EMPTY})
     * to prevent NullPointerExceptions in subsequent logistics operations.
     * </p>
     *
     * @param index   The zero-based index of the slot to overwrite.
     * @param content The new content to forcefully inject, or null to empty the slot.
     */
    void setContent(int index, D content);

    /**
     * @return The index of the first slot that contains valid, non-empty data, or -1 if entirely empty.
     */
    int findFirstIndex();

    /**
     * Finds the index of a specific resource type.
     *
     * @param target     The reference data to search for.
     * @param ignoreFull If true, skips slots that have already reached their maximum capacity.
     * @return The target index, or -1 if no suitable slot is found.
     */
    int findIndexOfTarget(D target, boolean ignoreFull);

    /**
     * Internal lifecycle hook triggered immediately after any physical contents or capacity states are mutated.
     * <p>
     * <b>ARCHITECTURAL CONTRACT:</b> This method serves as the central notification hub for the Observer pattern.
     * Implementations MUST use this hook to flag the underlying ECS Component as dirty,
     * trigger Server-to-Client state synchronization (RPC), or update subscribed GUI listeners.
     * </p>
     */
    void onContentsChanged();

    /**
     * @return The maximum mathematical capacity of this container.
     */
    long getCapacity();

    /**
     * @return The cumulative stored quantity across all slots.
     */
    long getAllContentQuantity();

    /**
     * @return The underlying validation predicate defining what data this container accepts.
     */
    default Predicate<D> getValidator() {
        return _ -> true;
    }
}
