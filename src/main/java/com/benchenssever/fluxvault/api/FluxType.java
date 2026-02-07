package com.benchenssever.fluxvault.api;

import com.benchenssever.fluxvault.item.ItemFlux;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.hypixel.hytale.server.core.inventory.ItemStack;

/**
 * Represents the unique identity and type definition for a specific Flux resource system.
 * <p>
 * This class serves as a safe runtime token that binds a Carrier type ({@code T})
 * with its corresponding Data type ({@code D}). It allows for type-safe casting and
 * retrieval of handlers from providers.
 * </p>
 *
 * @param <T> The type of the Carrier (e.g., {@code LiquidFlux}).
 * @param <D> The type of the Data (e.g., {@code LiquidStack}).
 */
public final class FluxType<T extends IFlux<T, D>, D> {

    /**
     * Standard FluxType instance for Liquid resources.
     */
    public static final FluxType<LiquidFlux, LiquidStack> LIQUID = new FluxType<>("LIQUID", LiquidFlux.class, LiquidStack.class);
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
     * Gets the unique name of this FluxType.
     *
     * @return The name identifier.
     */
    public String getName() {
        return name;
    }

    /**
     * Safely casts a generic IFlux carrier to the specific type {@code T}.
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
     * Safely casts a generic object to the specific data type {@code D}.
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
     * Casts a wildcard handler to a strictly typed handler associated with this FluxType.
     * <p>
     * This is an unchecked cast used to bridge generic API calls (e.g., from {@link IFluxProvider})
     * to specific implementations.
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
