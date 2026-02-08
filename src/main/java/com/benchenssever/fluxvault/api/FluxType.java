package com.benchenssever.fluxvault.api;

import com.benchenssever.fluxvault.item.ItemFlux;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.hypixel.hytale.server.core.inventory.ItemStack;

/**
 * Identity token binding a Carrier type (T) to its Data type (D).
 * <p>
 * Used for safe runtime casting, type comparison, and retrieving specific handlers.
 * </p>
 *
 * @param <T> The Carrier implementation (e.g., {@code LiquidFlux}).
 * @param <D> The Data type (e.g., {@code LiquidStack}).
 */
public final class FluxType<T extends IFlux<T, D>, D> {

    /** Standard type for Liquid resources. */
    public static final FluxType<LiquidFlux, LiquidStack> LIQUID = new FluxType<>("LIQUID", LiquidFlux.class, LiquidStack.class);

    /** Standard type for Item resources. */
    public static final FluxType<ItemFlux, ItemStack> ITEM = new FluxType<>("ITEM", ItemFlux.class, ItemStack.class);

    private final String name;
    private final Class<T> resourceClass;
    private final Class<D> dataClass;

    private FluxType(String name, Class<T> resourceClass, Class<D> dataClass) {
        this.name = name;
        this.resourceClass = resourceClass;
        this.dataClass = dataClass;
    }

    /**
     * @return The unique identifier name.
     */
    public String getName() {
        return name;
    }

    /**
     * Casts a generic IFlux interface to the concrete carrier type {@code T}.
     *
     * @param flux The carrier to cast.
     * @return The cast carrier.
     * @throws ClassCastException if the flux is not an instance of {@code T}.
     */
    public T castResource(IFlux<T, D> flux) {
        if (resourceClass.isInstance(flux)) {
            return resourceClass.cast(flux);
        }
        throw new ClassCastException("Flux type mismatch!");
    }

    /**
     * Casts a generic object to the specific data type {@code D}.
     *
     * @param data The object to cast.
     * @return The cast data object.
     * @throws ClassCastException if the object is not an instance of {@code D}.
     */
    public D castData(Object data) {
        if (dataClass.isInstance(data)) {
            return dataClass.cast(data);
        }
        throw new ClassCastException("Data type mismatch!");
    }

    /**
     * Unchecked cast of a generic handler to the specific type {@code <T, D>}.
     * <p>
     * Used to bridge generic API calls to specific implementations.
     * </p>
     *
     * @param handler The handler to cast.
     * @return The handler typed to {@code <T, D>}.
     */
    @SuppressWarnings("unchecked")
    public IFluxHandler<T, D> castHandler(IFluxHandler<?, ?> handler) {
        return (IFluxHandler<T, D>) handler;
    }

    @Override
    public String toString() {
        return "FluxType{" + "name='" + name + '\'' + ", T=" + resourceClass.getSimpleName() + ", D=" + dataClass.getSimpleName() + '}';
    }
}
