/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.popo70023.fluxvault.energy.EnergyFlux;
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import io.github.popo70023.fluxvault.item.ItemFlux;
import io.github.popo70023.fluxvault.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.liquid.LiquidStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Identity token binding a Carrier type (F) to its Data type (D).
 * <p>
 * Used for safe runtime casting, type comparison, and retrieving specific handlers.
 * </p>
 *
 * @param <F> The Carrier implementation (e.g., {@code LiquidFlux}).
 * @param <D> The Data type (e.g., {@code LiquidStack}).
 */
public final class FluxType<F extends IFlux<D>, D> {
    private static final Map<String, FluxType<?, ?>> REGISTRY = new ConcurrentHashMap<>();

    public static final FluxType<ItemFlux, ItemStack> ITEM = register("ITEM", ItemFlux.class, ItemStack.class);
    public static final FluxType<LiquidFlux, LiquidStack> LIQUID = register("LIQUID", LiquidFlux.class, LiquidStack.class);
    public static final FluxType<EnergyFlux, FluxEnergy> FLUX_ENERGY = register("FLUX_ENERGY", EnergyFlux.class, FluxEnergy.class);

    private final String name;
    private final Class<F> resourceClass;
    private final Class<D> dataClass;

    private FluxType(String name, Class<F> resourceClass, Class<D> dataClass) {
        this.name = name;
        this.resourceClass = resourceClass;
        this.dataClass = dataClass;
    }

    /**
     * Factory method that creates and securely registers a new FluxType into the global registry.
     * <p>
     * This guarantees that all instantiated FluxTypes are properly tracked by the system.
     * </p>
     * * @param name          The unique identifier name.
     *
     * @param resourceClass The Class object of the carrier.
     * @param dataClass     The Class object of the data payload.
     * @throws IllegalArgumentException if a type with the same name already exists.
     */
    public static <T extends IFlux<V>, V> FluxType<T, V> register(String name, Class<T> resourceClass, Class<V> dataClass) {
        FluxType<T, V> newType = new FluxType<>(name, resourceClass, dataClass);
        if (REGISTRY.putIfAbsent(name, newType) != null) {
            throw new IllegalArgumentException("FluxType already registered with name: " + name);
        }
        return newType;
    }

    /**
     * Retrieves a registered FluxType by its string identifier.
     * Used for deserialization and network RPC payload reconstruction.
     *
     * @param name The unique identifier name.
     * @return The FluxType, or null if not found.
     */
    public static FluxType<?, ?> getByName(String name) {
        return REGISTRY.get(name);
    }

    /**
     * @return The unique identifier name.
     */
    public String getName() {
        return name;
    }

    public Class<F> getResourceClass() {
        return resourceClass;
    }

    /**
     * Unchecked cast of a generic handler to the specific type {@code <F, D>}.
     * <p>
     * Used to bridge generic API calls to specific implementations.
     * </p>
     *
     * @param handler The handler to cast.
     * @return The handler typed to {@code <F, D>}.
     */
    @SuppressWarnings("unchecked")
    public IFluxHandler<F> castHandler(IFluxHandler<?> handler) {
        return (IFluxHandler<F>) handler;
    }

    @Override
    public String toString() {
        return "FluxType{" + "name='" + name + '\'' + ", F=" + resourceClass.getSimpleName() + ", D=" + dataClass.getSimpleName() + '}';
    }
}
