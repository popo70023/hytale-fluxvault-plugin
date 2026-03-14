package com.benchenssever.fluxvault.api;

import java.util.List;
import java.util.function.Predicate;

/**
 * A handler that exposes internal storage state for inspection and direct manipulation.
 * Used for GUIs, rendering, debugging, or complex automation logic.
 *
 * @param <D> The data type.
 */
public interface IFluxContainer<D> {

    /**
     * @return The maximum number of distinct stacks (slots) this getContainer can hold.
     */
    int getContainerMaxSize();

    /**
     * Retrieves a snapshot view of contents.
     * Note: May contain nulls or empty stacks depending on implementation.
     */
    List<D> getContents();

    /**
     * Retrieves content in a specific slot. Treat as read-only.
     */
    D getContent(int index);

    /**
     * Directly sets content in a slot. Bypasses standard I/O checks.
     * Used for creative mode, editors, or exact syncing.
     */
    void setContent(int index, D content);

    /**
     * @return Finds the index of the first non-empty stack, or -1.
     */
    int findFirstIndex();

    /**
     * Finds the index of a specific resource.
     *
     * @return The index, or -1 if not found.
     */
    int findIndexOfTarget(D target, boolean ignoreFull);

    /**
     * Callback triggered when contents change.
     */
    void onContentsChanged();

    /**
     * @return The max capacity of this getContainer.
     */
    long getContainerCapacity();

    /**
     * @return The total stored quantity combined.
     */
    long getAllContentQuantity();

    /**
     * @return The validation predicate for allowed contents.
     */
    default Predicate<D> getValidator() {
        return _ -> true;
    }

    enum CapacityType {
        FINITE,
        INFINITE_CAPACITY,
        INFINITE_CONTENT;

        public boolean finite() {
            return this == FINITE;
        }

        public boolean infiniteCapacity() {
            return this == INFINITE_CAPACITY;
        }

        public boolean infiniteContent() {
            return this == INFINITE_CONTENT;
        }
    }
}
