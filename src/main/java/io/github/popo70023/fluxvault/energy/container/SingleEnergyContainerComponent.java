package io.github.popo70023.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.*;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SingleEnergyContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final String ID = "SingleEnergyContainerComponent";
    public static final BuilderCodec<SingleEnergyContainerComponent> CODEC = BuilderCodec.builder(SingleEnergyContainerComponent.class, SingleEnergyContainerComponent::new)
            .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, FluxEnergy.CODEC), SingleEnergyContainerComponent::setContent, SingleEnergyContainerComponent::getContent)
            .documentation(EnergyContainer.CONTENT_DOCUMENTATION).add()
            .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SingleEnergyContainerComponent::setCapacity, SingleEnergyContainerComponent::getCapacity)
            .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
            .build();
    private final SingleEnergyContainer container;

    public SingleEnergyContainerComponent() {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), 10000);
    }

    public SingleEnergyContainerComponent(FluxEnergy content, long capacity) {
        this.container = new SingleEnergyContainer(content, capacity);
    }

    public SingleEnergyContainerComponent(long capacity) {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), capacity);
    }

    public static ComponentType<ChunkStore, SingleEnergyContainerComponent> getComponentType() {
        return ComponentTypes.SINGLE_ENERGY_CONTAINER;
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
    public <T extends IFlux<D>, D> IFluxHandler<T> getFluxHandler(FluxType<T, D> type) {
        if (type == FluxType.FLUX_ENERGY) {
            return type.castHandler(this.container);
        }
        return null;
    }

    @NullableDecl
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<ChunkStore> clone() {
        return new SingleEnergyContainerComponent(
                this.getContent().copy(),
                this.getCapacity()
        );
    }
}
