package com.benchenssever.fluxvault.api;

import java.util.List;
import java.util.function.Predicate;

/**
 * A handler that exposes internal storage state for inspection and direct manipulation.
 * Used for GUIs, rendering, debugging, or complex automation logic.
 *
 * @param <T> The carrier type.
 * @param <D> The data type.
 */
public interface IFluxContainer<T extends IFlux<T, D>, D> extends IFluxHandler<T, D> {

    /**
     * @return The maximum number of distinct stacks (slots) this container can hold.
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
     * Attempts to add content, merging if possible.
     *
     * @return The remainder that couldn't fit.
     */
    D addContent(D content);

    /**
     * @return The index of the first non-empty stack, or -1.
     */
    int getFirstContentIndex();

    /**
     * Finds the index of a specific resource (ignoring quantity).
     * @return The index, or -1 if not found.
     */
    int findContentIndex(D content);

    /**
     * Callback triggered when contents change.
     */
    void onContentsChanged();

    /**
     * @return The max capacity of this container.
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
}
