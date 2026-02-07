package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * Defines a generic resource carrier (Flux).
 * <p>
 * This interface represents a "Packet" or "Bundle" that transports specific resource data.
 * It separates the concept of the <b>Carrier</b> (the object moving through pipes)
 * from the <b>Data</b> (the actual resource content).
 * </p>
 *
 * @param <T> The type of the Carrier itself (Self-type pattern), used for chaining and copying. e.g., {@code LiquidFlux}
 * @param <D> The type of the Data contained within. e.g., {@code LiquidStack}
 */
public interface IFlux<T extends IFlux<T, D>, D> extends Iterable<D> {

    /**
     * Retrieves the identity token for this Flux type.
     * <p>
     * This is used by handlers to verify if they can accept this specific type of carrier.
     * </p>
     *
     * @return The FluxType containing type information for both T and D.
     */
    FluxType<T, D> getFluxType();

    /**
     * returns the number of data stacks contained in this carrier.
     * <ul>
     * <li>For {@code FluxPacket} (Single), this returns 1.</li>
     * <li>For {@code FluxBundle} (Batch), this returns the list size.</li>
     * </ul>
     *
     * @return The count of individual data stacks.
     */
    int getStackSize();

    /**
     * Retrieves the primary data stack.
     * <p>
     * For a single packet, this returns the only content.
     * For a bundle, this returns the first element in the list.
     * </p>
     *
     * @return The first data stack, or {@code null} if the bundle is empty.
     */
    D getFirstStack();

    /**
     * Retrieves a list view of all data stacks in this carrier.
     * <p>
     * Useful for accessing contents by index or inspecting the entire payload.
     * The returned list is typically unmodifiable.
     * </p>
     *
     * @return A list containing all data stacks.
     */
    List<D> getStacks();

    /**
     * Calculates the total quantity of all data within this carrier.
     * <p>
     * The specific summation logic depends on the implementation of D.
     * </p>
     *
     * @return The total quantity.
     */
    long getAllQuantity();

    /**
     * Checks if this carrier contains no valid data.
     *
     * @return {@code true} if the carrier is empty or contains zero quantity; {@code false} otherwise.
     */
    boolean isEmpty();

    /**
     * Retrieves the additional validation logic for this carrier.
     * <p>
     * This is an in-memory filter used for complex conditions (e.g., specific NBT values, temperature, voltage)
     * that cannot be expressed by simple data types.
     * </p>
     * <p>
     * Defaults to an "always true" predicate to avoid null checks.
     * </p>
     *
     * @return The validation predicate. Never null.
     */
    @Nonnull
    default Predicate<D> getValidator() {
        return _ -> true;
    }

    /**
     * Sets the validation logic for this carrier.
     * <p>
     * This method is designed for fluent chaining (e.g., {@code flux.withValidator(s -> s.getTemp() > 300)}).
     * </p>
     * <p>
     * <b>Note:</b> The validator is transient and typically exists only in server memory.
     * It is NOT serialized for network transmission or disk storage.
     * </p>
     *
     * @param validator The predicate to apply.
     * @return The carrier instance itself (this) for chaining.
     */
    T withValidator(Predicate<D> validator);

    /**
     * Checks if the provided target data satisfies the requirements of this carrier.
     * <p>
     * This method delegates the check to the predicate returned by {@link #getValidator()}.
     * Implementing classes may override this to include additional baseline checks (e.g., type matching).
     * </p>
     *
     * @param targetData The data to check.
     * @return {@code true} if the data is valid according to the validator; {@code false} otherwise.
     */
    default boolean matches(D targetData) {
        if (targetData == null) return false;
        return getValidator().test(targetData);
    }

    /**
     * Creates a deep copy of this carrier.
     * <p>
     * This ensures that the state of the original object is not modified
     * when passing it through handlers or pipes.
     * </p>
     *
     * @return A new, independent instance of this carrier.
     */
    T copy();
}
