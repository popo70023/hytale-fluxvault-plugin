package com.benchenssever.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFlux;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.api.IFluxProvider;
import com.benchenssever.fluxvault.energy.FluxEnergy;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SingleEnergyContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final BuilderCodec<SingleEnergyContainerComponent> CODEC = BuilderCodec.builder(SingleEnergyContainerComponent.class, SingleEnergyContainerComponent::new)
            .append(new KeyedCodec<>("Content", FluxEnergy.CODEC), SingleEnergyContainerComponent::setContent, SingleEnergyContainerComponent::getContent).add()
            .append(new KeyedCodec<>("Capacity", Codec.LONG), SingleEnergyContainerComponent::setCapacity, SingleEnergyContainerComponent::getCapacity).add()
            .append(new KeyedCodec<>("CapacityType", Codec.STRING), SingleEnergyContainerComponent::setCapacityType, SingleEnergyContainerComponent::getCapacityType).add()
            .build();
    private final SingleEnergyContainer container;

    public SingleEnergyContainerComponent() {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), 10000, "FINITE");
    }

    public SingleEnergyContainerComponent(FluxEnergy content, long capacity, String capacityType) {
        this.container = new SingleEnergyContainer(content, capacity, capacityType);
    }

    public SingleEnergyContainerComponent(long capacity, String capacityType) {
        this.container = new SingleEnergyContainer(FluxEnergy.of(0), capacity, capacityType);
    }

    public FluxEnergy getContent() {
        return this.container.getContent(0);
    }

    public void setContent(FluxEnergy content) {
        this.container.setContent(0, content);
    }

    public long getCapacity() {
        return this.container.getContainerCapacity();
    }

    public void setCapacity(long capacity) {
        this.container.setContainerCapacity(capacity);
    }

    public String getCapacityType() {
        return this.container.getCapacityType();
    }

    public void setCapacityType(String capacityTypeStr) {
        this.container.setCapacityType(capacityTypeStr);
    }

    @NullableDecl
    @Override
    public <T extends IFlux<T, D>, D> IFluxHandler<T, D> getFluxHandler(FluxType<T, D> type) {
        if (type == FluxType.FLUX_ENERGY) {
            return type.castHandler(this.container);
        }
        return null;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new SingleEnergyContainerComponent(
                this.getContent().copy(),
                this.getCapacity(),
                this.getCapacityType()
        );
    }
}
