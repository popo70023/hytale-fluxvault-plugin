/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import io.github.popo70023.fluxvault.energy.container.EnergyContainer;
import io.github.popo70023.fluxvault.energy.container.SingleEnergyContainer;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SimpleEnergyContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final String ID = "SimpleEnergyContainerComponent";
    public static final BuilderCodec<SimpleEnergyContainerComponent> CODEC = BuilderCodec.builder(SimpleEnergyContainerComponent.class, SimpleEnergyContainerComponent::new)
            .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, FluxEnergy.CODEC), SimpleEnergyContainerComponent::setContent, SimpleEnergyContainerComponent::getContent)
            .documentation(EnergyContainer.CONTENT_DOCUMENTATION).add()
            .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SimpleEnergyContainerComponent::setCapacity, SimpleEnergyContainerComponent::getCapacity)
            .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
            .build();
    private final SingleEnergyContainer container;

    public SimpleEnergyContainerComponent() {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), 10000);
    }

    public SimpleEnergyContainerComponent(FluxEnergy content, long capacity) {
        this.container = new SingleEnergyContainer(content, capacity);
    }

    public SimpleEnergyContainerComponent(long capacity) {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), capacity);
    }

    public static ComponentType<ChunkStore, SimpleEnergyContainerComponent> getComponentType() {
        return ComponentTypes.SIMPLE_ENERGY_CONTAINER;
    }

    public FluxEnergy getContent() {
        return this.container.getContent();
    }

    public void setContent(FluxEnergy content) {
        this.container.setContent(content);
    }

    public long getCapacity() {
        return this.container.getCapacity();
    }

    public void setCapacity(long capacity) {
        this.container.setCapacity(capacity);
    }

    @NullableDecl
    @Override
    public <T extends IFlux<D>, D> IFluxHandler<T> getFluxHandler(@NonNullDecl FluxType<T, D> type, @NonNullDecl BlockFace side, @NonNullDecl FluxAccess access) {
        if (container.matchesFluxType(type)) {
            return type.castHandler(container);
        }
        return null;
    }

    @NullableDecl
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<ChunkStore> clone() {
        return new SimpleEnergyContainerComponent(
                this.getContent().copy(),
                this.getCapacity()
        );
    }
}
