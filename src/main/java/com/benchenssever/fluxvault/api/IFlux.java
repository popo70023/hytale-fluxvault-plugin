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
 * @param <T> The carrier type (Self-type).
 * @param <D> The data type (e.g., LiquidStack).
 */
public interface IFlux<T extends IFlux<T, D>, D> extends Iterable<D> {

    /**
     * @return The identity token for this Flux type.
     */
    FluxType<T, D> getFluxType();

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
     * @return The stack, or an empty/null value if out of bounds.
     */
    D getStack(int index);

    /**
     * Replaces the stack at the specified index.
     */
    void setStack(int index, D stack);

    /**
     * Appends a stack to the end of the payload.
     */
    void addStack(D stack);

    /**
     * Removes all empty stacks from the payload.
     * Useful for compacting the list after modifications.
     */
    void cleanFlux();

    /**
     * @return The total quantity of all data combined.
     */
    long getAllQuantity();

    /**
     * @return True if the carrier contains no valid data.
     */
    boolean isEmpty();

    /**
     * @return The transient validation predicate. Never null.
     */
    @Nonnull
    default Predicate<D> getValidator() {
        return _ -> true;
    }

    /**
     * Sets a transient validator for this carrier.
     * @return This instance for chaining.
     */
    T withValidator(Predicate<D> validator);

    /**
     * Checks if the target data matches this carrier's validator.
     */
    default boolean matches(D targetData) {
        if (targetData == null) return false;
        return getValidator().test(targetData);
    }

    /**
     * @return A deep copy of this carrier.
     */
    T copy();
}
