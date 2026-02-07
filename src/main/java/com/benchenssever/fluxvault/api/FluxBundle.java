package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract base class for a composite Flux carrier (Bundle).
 * <p>
 * Designed for bulk transport, capable of holding multiple stacks of data simultaneously.
 * Suitable for logic like batch processing or multi-type pipelines.
 * </p>
 *
 * @param <T> The concrete implementation type (e.g., LiquidFlux).
 * @param <D> The data type (e.g., LiquidStack).
 */
public abstract class FluxBundle<T extends IFlux<T, D>, D> implements IFlux<T, D> {

    /**
     * Internal storage for data stacks.
     */
    protected final List<D> stacks;
    /**
     * Internal storage for the transient validation logic.
     * <p>
     * Applies to the bundle as a whole or individual elements depending on implementation.
     * Initialized to "always true".
     * </p>
     */
    @Nonnull
    protected Predicate<D> validator = _ -> true;

    /**
     * Constructs a bundle from an array of stacks (Varargs).
     *
     * @param stacks The data stacks to include.
     */
    @SafeVarargs
    public FluxBundle(D... stacks) {
        this.stacks = new ArrayList<>(List.of(stacks));
    }

    /**
     * Constructs a bundle from a list of stacks.
     *
     * @param stacks The list of data stacks.
     */
    public FluxBundle(List<D> stacks) {
        this.stacks = new ArrayList<>(stacks);
    }

    @Override
    public int getStackSize() {
        return stacks.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * <b>Implementation Note:</b> Subclasses must implement this method to retrieve the first
     * <b>valid (non-empty)</b> stack from the internal list.
     * </p>
     * <p>
     * Since a bundle might contain empty stacks (e.g., results from a partial drain),
     * the implementation should iterate through {@code stacks} and skip any empty entries,
     * returning the first substantial resource found, or {@code null} if all stacks are empty.
     * </p>
     *
     * @return The first non-empty data stack, or {@code null} if no valid data exists.
     */
    @Override
    abstract public D getFirstStack();

    /**
     * Returns an unmodifiable view of the internal stack list.
     *
     * @return An immutable list of data stacks.
     */
    @Override
    public List<D> getStacks() {
        return Collections.unmodifiableList(stacks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Predicate<D> getValidator() {
        return validator;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation updates the internal validator field and casts {@code this}
     * to the concrete type {@code T} to allow for fluent method chaining.
     * </p>
     *
     * @param validator The new validation logic. If null, it defaults to "always true".
     * @return The carrier instance itself (this).
     */
    @Override
    @SuppressWarnings("unchecked")
    public T withValidator(Predicate<D> validator) {
        this.validator = (validator != null) ? validator : _ -> true;
        return (T) this;
    }

    /**
     * Provides an iterator to traverse all stacks in this bundle.
     *
     * @return An iterator for the internal list.
     */
    @Override
    public Iterator<D> iterator() {
        return stacks.iterator();
    }
}
