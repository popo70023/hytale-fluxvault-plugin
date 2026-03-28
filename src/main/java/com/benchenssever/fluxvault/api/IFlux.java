package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a generic carrier (Packet/Bundle) that transports resources.
 * <p>
 * This interface is mutable to support logistics operations (e.g., checking off items from a request list,
 * reducing quantities as the payload travels through a pipe network).
 * </p>
 *
 * @param <D> The data type (e.g., LiquidStack, FluxEnergy).
 */
public interface IFlux<D> {

    /**
     * Retrieves the total number of slots/stacks available in this carrier.
     *
     * @return The number of stacks in this carrier. For Packets, this is typically 1.
     */
    int getStackCount();

    /**
     * Retrieves an unmodifiable view of all contained stacks.
     *
     * @return A list view of all stacks. Modifications to the list itself will throw an exception,
     * but the underlying elements may still be mutable depending on implementation.
     */
    List<D> getStacks();

    /**
     * Retrieves the stack at the specified index.
     *
     * @param index The zero-based index of the stack.
     * @return The stack, or null/empty equivalent if the index is out of bounds or the slot is empty.
     */
    D getStack(int index);

    /**
     * Finds the first index containing a stack that satisfies the given condition.
     *
     * @param validator The condition to test each stack against.
     * @return The index of the first matching stack, or -1 if no matches are found or the carrier is empty.
     */
    int findIndexOfFirstMatchesStack(Predicate<D> validator);

    /**
     * Finds the first index containing a stack that is equivalent to the target data.
     * Equivalence is defined by the underlying data type (e.g., matching item types, ignoring quantities).
     *
     * @param target The reference data to search for.
     * @return The index of the first equivalent stack, or -1 if not found.
     */
    int findIndexOfTarget(D target);

    /**
     * Replaces the stack at the specified index with the provided stack.
     *
     * @param index The zero-based index to modify.
     * @param stack The new stack to insert. Passing null typically equates to clearing the slot.
     * @return The stack that was just inserted, for operation chaining.
     * @throws IndexOutOfBoundsException if the carrier is a strict Packet and the index > 0.
     */
    D setStack(int index, D stack);

    /**
     * Appends a stack to the end of the payload, or merges it into an existing equivalent stack
     * if the underlying implementation supports auto-merging.
     *
     * @param stack The data stack to append.
     * @return This carrier instance, to allow for fluent method chaining.
     */
    IFlux<D> addStack(D stack);

    /**
     * Removes and returns the stack at the specified index, effectively leaving that slot empty.
     *
     * @param index The zero-based index to clear.
     * @return The stack that was removed, or null/empty if the slot was already empty.
     */
    D removeStack(int index);

    /**
     * Calculates the cumulative quantity of all data elements across all slots in this carrier.
     *
     * @return The total quantity of all data combined. Returns 0 if empty.
     */
    long getAllQuantity();

    /**
     * Checks if this carrier contains absolutely no resources.
     *
     * @return True if all slots are empty or contain 0 quantity; otherwise false.
     */
    boolean isEmpty();

    /**
     * Checks if a specific slot in this carrier is empty.
     *
     * @param index The zero-based index to check.
     * @return True if the specified index contains no valid data or 0 quantity.
     */
    boolean isIndexEmpty(int index);

    /**
     * Retrieves the maximum allowed transfer rate for this specific carrier operation.
     * Used by handlers to cap the amount of resources processed in a single tick.
     *
     * @return The transfer limit.
     */
    long getTransferLimit();

    /**
     * Imposes a strict mathematical ceiling on how much data this carrier is allowed to transfer.
     *
     * @param limit The maximum quantity allowed. Negative values should be clamped to 0.
     */
    void setTransferLimit(long limit);

    /**
     * A fluent alternative to {@link #setTransferLimit(long)}.
     * <p>
     * Applies the transfer limit and returns the current instance to facilitate method chaining.
     * </p>
     *
     * @param limit The maximum quantity allowed.
     * @return This carrier instance, for operation chaining.
     */
    default IFlux<D> withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    /**
     * Retrieves the transient validation logic bound to this carrier.
     * Used predominantly in drain requests to filter out unwanted resources dynamically.
     *
     * @return The transient validation predicate. Guaranteed to never be null.
     */
    @Nonnull
    default Predicate<D> getValidator() {
        return _ -> true;
    }

    /**
     * Attaches a transient validation filter to this carrier.
     *
     * @param validator The condition to apply. If null is passed, it should default to accepting everything.
     */
    void setValidator(Predicate<D> validator);

    /**
     * A fluent alternative to {@link #setValidator(Predicate)}.
     * <p>
     * Applies the transient validation filter and returns the current instance to facilitate method chaining.
     * </p>
     *
     * @param validator The condition to apply.
     * @return This carrier instance, for operation chaining.
     */
    default IFlux<D> withValidator(Predicate<D> validator) {
        setValidator(validator);
        return this;
    }

    /**
     * Tests whether the provided target data satisfies this carrier's internal validator.
     *
     * @param targetData The external data to test.
     * @return True if the data passes the validation logic. Always returns false if targetData is null.
     */
    default boolean matchesWithFlux(D targetData) {
        if (targetData == null) return false;
        return getValidator().test(targetData);
    }

    /**
     * Verifies if an external reference object is structurally/semantically equivalent to the stack
     * residing at the specified index within this carrier.
     *
     * @param index     The zero-based index of the internal stack to compare against.
     * @param reference The external data to verify.
     * @return True if they match based on the type's definition of equivalence.
     */
    boolean matchesWithIndex(int index, D reference);

    /**
     * Creates an absolute, independent duplicate of this carrier and all its internal stacks.
     * Modifying the returned copy MUST NOT affect the original carrier.
     *
     * @return A deep copy of this carrier.
     */
    IFlux<D> copy();
}
