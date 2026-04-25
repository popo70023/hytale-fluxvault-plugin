/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import io.github.popo70023.fluxvault.common.flux.transaction.IFluxTransaction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;

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
 * @param <D> The data type (e.g., LiquidStack, ResourceStack).
 */
public interface IFluxContainer<D> {

    /**
     * @return The maximum number of distinct stacks (slots) this container can physically hold.
     */
    short getContainerMaxSize();

    /**
     * Retrieves a sparse map snapshot of all valid contents.
     * <p>
     * <b>PERFORMANCE & MUTABILITY WARNING:</b> This returns an Short2ObjectMap to support sparse
     * inventory storage (e.g., items only in slots 0 and 100). The map and its elements MUST be
     * treated as strictly read-only.
     * </p>
     *
     * @return A map of slot indices to their respective content. Never null, but may be empty.
     */
    Short2ObjectMap<D> getContents();

    /**
     * Retrieves the exact content residing in a specific slot.
     * <p>
     * <b>MUTABILITY WARNING:</b> Treat the returned object as strictly read-only.
     * </p>
     *
     * @param index The zero-based index of the slot.
     * @return The content in the slot, or the system's empty equivalent.
     */
    D getContent(short index);

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
    void setContent(short index, D content);

    /**
     * @return The index of the first slot that contains valid, non-empty data, or -1 if entirely empty.
     */
    short findFirstIndex();

    /**
     * Finds the index of a specific resource type.
     *
     * @param target     The reference data to search for.
     * @param ignoreFull If true, skips slots that have already reached their maximum capacity.
     * @return The target index, or -1 if no suitable slot is found.
     */
    short findIndexOfTarget(D target, boolean ignoreFull);

    /**
     * Internal lifecycle hook triggered immediately after physical contents are mutated.
     * <p>
     * <b>ARCHITECTURAL CONTRACT:</b>
     * Implementations MUST use this hook to fire specific update events based on the provided
     * transaction. Listeners (like GUIs) can use the transaction details to perform highly
     * optimized partial UI updates instead of full redraws.
     * </p>
     * * @param transaction The exact record of what slots were modified.
     */
    void onContentsChanged(IFluxTransaction transaction);

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

    default boolean matchesWithContainer(D targetData) {
        if (targetData == null) return false;
        return getValidator().test(targetData);
    }
}
