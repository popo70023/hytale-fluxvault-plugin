/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux;

import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.event.EventRegistration;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.SyncEventBusRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.common.flux.transaction.IFluxTransaction;

import java.util.function.Consumer;

public class AbstractVault {
    protected static final HytaleLogger LOGGER = FluxVaultPlugin.getPluginLogger();

    protected final transient SyncEventBusRegistry<Void, ChangeEvent> externalChangeEventRegistry;
    protected final transient SyncEventBusRegistry<Void, ChangeEvent> internalChangeEventRegistry;

    public AbstractVault() {
        this.externalChangeEventRegistry = new SyncEventBusRegistry<>(LOGGER, ChangeEvent.class);
        this.internalChangeEventRegistry = new SyncEventBusRegistry<>(LOGGER, ChangeEvent.class);

        this.internalChangeEventRegistry.register(EventPriority.NORMAL.getValue(), null, this::aggregateAndBroadcast);
    }

    public EventRegistration<Void, ChangeEvent> registerChangeEvent(Consumer<ChangeEvent> consumer) {
        return this.registerChangeEvent(EventPriority.NORMAL, consumer);
    }

    public EventRegistration<Void, ChangeEvent> registerChangeEvent(EventPriority priority, Consumer<ChangeEvent> consumer) {
        return this.externalChangeEventRegistry.register(priority.getValue(), null, consumer);
    }

    protected void aggregateAndBroadcast(ChangeEvent event) {
        this.externalChangeEventRegistry.dispatchFor(null).dispatch(event);
    }

    public record ChangeEvent(AbstractVault vault, IFluxTransaction transaction) implements IEvent<Void> {
        @Override
        public String toString() {
            return "SimpleLiquidContainerVaultChangeEvent{vault=" + vault + ", transaction=" + transaction + "}";
        }
    }
}
