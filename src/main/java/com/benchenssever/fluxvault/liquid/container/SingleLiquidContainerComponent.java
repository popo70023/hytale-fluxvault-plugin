package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFlux;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.api.IFluxProvider;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SingleLiquidContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final BuilderCodec<SingleLiquidContainerComponent> CODEC = BuilderCodec.builder(SingleLiquidContainerComponent.class, SingleLiquidContainerComponent::new)
            .append(new KeyedCodec<>("Content", LiquidStack.CODEC), SingleLiquidContainerComponent::setContent, SingleLiquidContainerComponent::getContent).add()
            .append(new KeyedCodec<>("Capacity", Codec.LONG), SingleLiquidContainerComponent::setCapacity, SingleLiquidContainerComponent::getCapacity).add()
            .append(new KeyedCodec<>("CapacityType", Codec.STRING), SingleLiquidContainerComponent::setCapacityType, SingleLiquidContainerComponent::getCapacityType).add()
            .append(new KeyedCodec<>("Tags", Codec.STRING_ARRAY), SingleLiquidContainerComponent::setSupportedTags, SingleLiquidContainerComponent::getSupportedTags).add()
            .build();
    private final SingleLiquidContainer container;

    public SingleLiquidContainerComponent() {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, 10000, "FINITE", new String[0]);
    }

    public SingleLiquidContainerComponent(LiquidStack content, long capacity, String capacityType, String[] supportedTags) {
        this.container = new SingleLiquidContainer(content, capacity, capacityType, supportedTags);
    }

    public SingleLiquidContainerComponent(long capacity, String capacityType, String[] supportedTags) {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, capacity, capacityType, supportedTags);
    }

    public LiquidStack getContent() {
        return this.container.getContent(0);
    }

    public void setContent(LiquidStack content) {
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

    public void setCapacityType(String capacityType) {
        this.container.setCapacityType(capacityType);
    }

    public String[] getSupportedTags() {
        return this.container.getSupportedTags();
    }

    public void setSupportedTags(String[] supportedTags) {
        this.container.setSupportedTags(supportedTags);
    }

    @NullableDecl
    @Override
    public <T extends IFlux<T, D>, D> IFluxHandler<T, D> getFluxHandler(FluxType<T, D> type) {
        if (type == FluxType.LIQUID) {
            return type.castHandler(this.container);
        }
        return null;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new SingleLiquidContainerComponent(
                this.container.getContent(0).copy(),
                this.container.getContainerCapacity(),
                this.container.getCapacityType(),
                this.container.getSupportedTags()
        );
    }
}
