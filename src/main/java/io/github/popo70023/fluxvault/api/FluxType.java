/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.payload.item.ItemFlux;
import io.github.popo70023.fluxvault.payload.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import io.github.popo70023.fluxvault.registry.FluxAssetRegistry;

import java.util.Collection;
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
    private static final Map<String, FluxType<ResourceFlux, ResourceStack>> REGISTRY_RESOURCE = new ConcurrentHashMap<>();

    public static final FluxType<ItemFlux, ItemStack> ITEM = new FluxType<>("Item", ItemFlux.class, ItemStack.class);
    public static final FluxType<LiquidFlux, LiquidStack> LIQUID = new FluxType<>("FluxVault.Liquid", LiquidFlux.class, LiquidStack.class);

    private final String name;
    private final Class<F> fluxClass;
    private final Class<D> dataClass;

    public FluxType(String name, Class<F> fluxClass, Class<D> dataClass) {
        this.name = name;
        this.fluxClass = fluxClass;
        this.dataClass = dataClass;
    }

    /**
     * Securely registers a {@code FluxType} instance into the global registry.
     * <p>
     * This guarantees that the provided FluxType is globally tracked and can be
     * retrieved by its identifier during network RPCs or deserialization.
     * </p>
     *
     * @param type The FluxType instance to register.
     * @throws IllegalArgumentException if a different FluxType instance with the same name is already registered.
     */
    public static void register(FluxType<?, ?> type) {
        FluxVaultPlugin.getPluginLogger().atInfo().log("Registering " + type.getName() + " for FluxType");
        FluxType<?, ?> existing = REGISTRY.get(type.getName());
        if (existing != null && existing != type) {
            throw new IllegalArgumentException("FluxType already registered with name: " + type.getName() + " by another instance!");
        }

        REGISTRY.put(type.getName(), type);
    }

    private static void registerResource(FluxType<ResourceFlux, ResourceStack> type) {
        register(type);
        REGISTRY_RESOURCE.put(type.getName(), type);
    }

    /**
     * Internal lifecycle method to register all default and asset-driven FluxTypes.
     * <p>
     * Called during the server initialization phase after assets are loaded.
     * </p>
     */
    public static void registerFluxTypes() {
        register(ITEM);
        register(LIQUID);
        Collection<FluxResource> fluxResources = FluxAssetRegistry.FLUX_RESOURCE_ASSET_STORE.getAssetMap().getAssetMap().values();
        for (FluxResource fluxResource : fluxResources) {
            FluxType<ResourceFlux, ResourceStack> newType = new FluxType<>(fluxResource.getId(), ResourceFlux.class, ResourceStack.class);
            registerResource(newType);
        }
    }

    /**
     * Clears the global registry cache.
     * <p>
     * Used primarily during server hot-reloads or shutdowns to prevent memory leaks
     * and allow clean re-registration of capabilities.
     * </p>
     */
    public static void clearCache() {
        REGISTRY.clear();
        REGISTRY_RESOURCE.clear();
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

    public static FluxType<ResourceFlux, ResourceStack> getResourceFluxTypeByName(String name) {
        return REGISTRY_RESOURCE.get(name);
    }

    /**
     * @return The unique identifier name.
     */
    public String getName() {
        return name;
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
        return "FluxType{" + "name='" + name + '\'' + ", F=" + fluxClass.getSimpleName() + ", D=" + dataClass.getSimpleName() + '}';
    }
}
