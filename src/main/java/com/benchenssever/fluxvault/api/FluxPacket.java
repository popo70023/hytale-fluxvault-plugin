package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Abstract base class for a singular Flux carrier (Packet).
 * <p>
 * Optimized for transporting a single stack of data. This is the most common form of transport
 * used in standard pipes and interactions.
 * </p>
 *
 * @param <T> The concrete implementation type.
 * @param <D> The data type.
 */
public abstract class FluxPacket<T extends IFlux<T, D>, D> implements IFlux<T, D> {

    private final D value;
    /**
     * Internal storage for the transient validation logic.
     * <p>
     * initialized to "always true" to prevent null pointer exceptions.
     * </p>
     */
    @Nonnull
    protected Predicate<D> validator = _ -> true;

    /**
     * Constructs a packet containing a single data value.
     *
     * @param value The data stack. Must not be null.
     * @throws IllegalArgumentException if value is null.
     */
    public FluxPacket(D value) {
        if (value == null) {
            throw new IllegalArgumentException("FluxPacket value cannot be null");
        }
        this.value = value;
    }

    @Override
    public int getStackSize() {
        return 1;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Since this is a single packet, it always returns the contained value.
     * </p>
     */
    @Override
    public D getFirstStack() {
        return value;
    }

    /**
     * Returns a singleton list containing the value.
     *
     * @return An immutable list containing the single data stack.
     */
    @Override
    public List<D> getStacks() {
        return List.of(value);
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
        this.validator = (validator != null) ? validator : data -> true;
        return (T) this;
    }

    /**
     * Provides an iterator for the single value.
     *
     * @return A singleton iterator.
     */
    @Override
    public Iterator<D> iterator() {
        return Collections.singleton(value).iterator();
    }
}
