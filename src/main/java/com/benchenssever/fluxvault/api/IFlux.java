package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a generic carrier (Packet/Bundle) that transports resources.
 * <p>
 * This interface is mutable to support logistics operations (e.g., checking off items from a request list).
 * </p>
 *
 * @param <D> The data type (e.g., LiquidStack).
 */
public interface IFlux<D> extends Iterable<D> {

    /**
     * @return The number of stacks in this carrier.
     */
    int getStackSize();

    /**
     * @return A list view of all stacks.
     */
    List<D> getStacks();

    /**
     * Retrieves the stack at the specified index.
     *
     * @return The stack, or an empty/null value if out of bounds.
     */
    D getStack(int index);

    int findIndexOfFirstMatchesStack(Predicate<D> validator);

    int findIndexOfTarget(D target);

    /**
     * Replaces the stack at the specified index.
     */
    D setStack(int index, D stack);

    /**
     * Appends stacks to the end of the payload.
     */
    IFlux<D> addStack(D... stacks);

    D removeStack(int index);

    /**
     * @return The total quantity of all data combined.
     */
    long getAllQuantity();

    /**
     * @return True if the carrier contains no valid data.
     */
    boolean isEmpty();

    boolean isIndexEmpty(int index);

    long getTransferLimit();

    void setTransferLimit(long limit);

    /**
     * @return The transient validation predicate. Never null.
     */
    @Nonnull
    default Predicate<D> getValidator() {
        return _ -> true;
    }

    /**
     * Sets a transient validator for this carrier.
     */
    void setValidator(Predicate<D> validator);

    /**
     * Checks if the target data matchesWithFlux this carrier's validator.
     */
    default boolean matchesWithFlux(D targetData) {
        if (targetData == null) return false;
        return getValidator().test(targetData);
    }

    boolean matchesWithIndex(int index, D reference);

    /**
     * @return A deep copy of this carrier.
     */
    IFlux<D> copy();
}
