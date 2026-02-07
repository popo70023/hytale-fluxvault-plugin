package com.benchenssever.fluxvault.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a handler that also exposes internal storage state.
 * <p>
 * While {@link IFluxHandler} handles interaction (I/O), {@code IFluxContainer}
 * allows inspection of the contents, often used for GUIs, rendering, or debug info.
 * </p>
 *
 * @param <T> The carrier type.
 * @param <D> The data type.
 */
public interface IFluxContainer<T extends IFlux<T, D>, D> extends IFluxHandler<T, D> {

    /**
     * Gets the maximum number of distinct stacks this container can hold.
     * <p>
     * For fixed inventories (Battery Box), this is the physical slot count.
     * For dynamic tanks (Smeltery), this might be {@link Integer#MAX_VALUE} or the current list size.
     * </p>
     *
     * @return The max slot count. Avoid using negative numbers.
     */
    int getContainerMaxSize();

    /**
     * Retrieves the data stack in a specific slot.
     * <p>
     * <b>Note:</b> The returned object should generally be treated as read-only.
     * Modification logic should be handled via {@link #fill} or {@link #drain}.
     * </p>
     *
     * @param index The slot index.
     * @return The content in the slot, or an empty stack/null depending on implementation.
     */
    D getContent(int index);

    /**
     * Retrieves a snapshot view of all contents in this container.
     * <p>
     * <b>Implementation Note:</b>
     * The structure of this list depends on the container type:
     * <ul>
     * <li><b>Slot-based (Inventory):</b> May contain empty stacks or nulls to preserve slot indices (e.g., for GUI rendering).</li>
     * <li><b>Volume-based (Tank):</b> Typically returns a compacted list of stored resources without gaps.</li>
     * </ul>
     * Callers should be prepared to handle empty or null entries when iterating this list.
     * </p>
     *
     * @return A list of all contents.
     */
    List<D> getContents();

    /**
     * Callback triggered when the contents of the container change.
     * <p>
     * Used for saving data, syncing with clients, or updating comparators.
     * </p>
     */
    void onContentsChanged();

    /**
     * Gets the maximum capacity of this container.
     * <p>
     * The interpretation of this value depends on {@link #isSharedCapacity()}.
     * </p>
     *
     * @return The max capacity.
     */
    long getContainerCapacity();

    /**
     * Determines if the capacity is shared across all contained fluids/items.
     *
     * @return {@code true} if capacity is a global pool (e.g., total volume shared by gases);
     * {@code false} if each slot has independent limits (default).
     */
    default boolean isSharedCapacity() {
        return false;
    }

    /**
     * Gets the total quantity of all stored resources combined.
     *
     * @return The total stored quantity.
     */
    long getAllContentQuantity();

    /**
     * Gets the internal validation logic for this container.
     * <p>
     * This predicate defines what this container is allowed to hold (e.g., voltage limits,
     * whitelist/blacklist filters). It is checked during {@link #fill} operations.
     * </p>
     *
     * @return The validation predicate. Defaults to "always true".
     */
    default Predicate<D> getValidator() {
        return _ -> true;
    }
}
