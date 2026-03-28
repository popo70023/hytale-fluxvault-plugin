package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.*;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.benchenssever.fluxvault.liquid.interaction.ui.LiquidContainerWindow;
import com.benchenssever.fluxvault.registry.ComponentTypes;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SingleLiquidContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final String ID = "SingleLiquidContainerComponent";
    public static final BuilderCodec<SingleLiquidContainerComponent> CODEC = BuilderCodec.builder(SingleLiquidContainerComponent.class, SingleLiquidContainerComponent::new)
            .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, LiquidStack.CODEC), SingleLiquidContainerComponent::setContent, SingleLiquidContainerComponent::getContent)
            .documentation(LiquidContainer.CONTENT_DOCUMENTATION).add()
            .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SingleLiquidContainerComponent::setCapacity, SingleLiquidContainerComponent::getCapacity)
            .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
            .append(new KeyedCodec<>(LiquidContainer.ACCEPTED_HAZARDS_KEY, Codec.STRING_ARRAY), SingleLiquidContainerComponent::setAcceptedHazards, SingleLiquidContainerComponent::getAcceptedHazards)
            .documentation(LiquidContainer.ACCEPTED_HAZARDS_DOCUMENTATION).add()
            .build();
    private final SingleLiquidContainer container;
    private final Map<UUID, LiquidContainerWindow> windows = new ConcurrentHashMap<>();

    public SingleLiquidContainerComponent() {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, 10000, new String[0]);
    }

    public SingleLiquidContainerComponent(LiquidStack content, long capacity, String[] supportedTags) {
        this.container = new SingleLiquidContainer(content, capacity, supportedTags);
    }

    public SingleLiquidContainerComponent(long capacity, String[] supportedTags) {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, capacity, supportedTags);
    }

    public static ComponentType<ChunkStore, SingleLiquidContainerComponent> getComponentType() {
        return ComponentTypes.SINGLE_LIQUID_CONTAINER;
    }

    public SingleLiquidContainer getContainer() {
        return this.container;
    }

    public LiquidStack getContent() {
        return this.container.getContent();
    }

    public void setContent(LiquidStack content) {
        this.container.setContent(content);
    }

    public long getCapacity() {
        return this.container.getCapacity();
    }

    public void setCapacity(long capacity) {
        this.container.setCapacity(capacity);
    }

    public String[] getAcceptedHazards() {
        return this.container.getAcceptedHazards();
    }

    private void setAcceptedHazards(String[] supportedTags) {
        this.container.setAcceptedHazards(supportedTags);
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(FluxType<F, D> type) {
        if (type == FluxType.LIQUID) {
            return type.castHandler(this.container);
        }
        return null;
    }

    @NullableDecl
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<ChunkStore> clone() {
        return new SingleLiquidContainerComponent(
                this.getContent().copy(),
                this.getCapacity(),
                this.getAcceptedHazards()
        );
    }

    public Map<UUID, LiquidContainerWindow> getActiveWindows() {
        return this.windows;
    }
}
