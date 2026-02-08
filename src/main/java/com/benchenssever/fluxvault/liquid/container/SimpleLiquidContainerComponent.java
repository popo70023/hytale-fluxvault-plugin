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

public class SimpleLiquidContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final BuilderCodec<SimpleLiquidContainerComponent> CODEC = BuilderCodec.builder(SimpleLiquidContainerComponent.class, SimpleLiquidContainerComponent::new)
            .append(new KeyedCodec<>("content", LiquidStack.CODEC), SimpleLiquidContainerComponent::setContent, SimpleLiquidContainerComponent::getContent).add()
            .append(new KeyedCodec<>("capacity", Codec.LONG), SimpleLiquidContainerComponent::setCapacity, SimpleLiquidContainerComponent::getCapacity).add()
            .append(new KeyedCodec<>("capacityType", Codec.STRING), SimpleLiquidContainerComponent::setCapacityType, SimpleLiquidContainerComponent::getCapacityType).add()
            .append(new KeyedCodec<>("tags", Codec.STRING_ARRAY), SimpleLiquidContainerComponent::setSupportedTags, SimpleLiquidContainerComponent::getSupportedTags).add()
            .build();
    private final SimpleLiquidContainer container;

    public SimpleLiquidContainerComponent() {
        this.container = new SimpleLiquidContainer(LiquidStack.EMPTY, 10000, "FINITE", new String[0]);
    }

    public SimpleLiquidContainerComponent(LiquidStack content, long capacity, String capacityType, String[] supportedTags) {
        this.container = new SimpleLiquidContainer(content, capacity, capacityType, supportedTags);
    }

    public LiquidStack getContent() {
        return this.container.getContainerContent();
    }

    public void setContent(LiquidStack content) {
        this.container.setContainerContent(content);
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
        return new SimpleLiquidContainerComponent(
                this.container.getContainerContent().copy(),
                this.container.getContainerCapacity(),
                this.container.getCapacityType(),
                this.container.getSupportedTags()
        );
    }
}
