/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.liquid.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.liquid.LiquidStack;
import io.github.popo70023.fluxvault.liquid.container.LiquidContainer;
import io.github.popo70023.fluxvault.liquid.container.SingleLiquidContainer;
import io.github.popo70023.fluxvault.liquid.interaction.ui.LiquidContainerWindow;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleLiquidContainerComponent implements Component<ChunkStore>, IFluxProvider {
    public static final String ID = "SingleLiquidContainerComponent";
    public static final BuilderCodec<SimpleLiquidContainerComponent> CODEC = BuilderCodec.builder(SimpleLiquidContainerComponent.class, SimpleLiquidContainerComponent::new)
            .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, LiquidStack.CODEC), SimpleLiquidContainerComponent::setContent, SimpleLiquidContainerComponent::getContent)
            .documentation(LiquidContainer.CONTENT_DOCUMENTATION).add()
            .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SimpleLiquidContainerComponent::setCapacity, SimpleLiquidContainerComponent::getCapacity)
            .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
            .append(new KeyedCodec<>(LiquidContainer.ACCEPTED_HAZARDS_KEY, Codec.STRING_ARRAY), SimpleLiquidContainerComponent::setAcceptedHazards, SimpleLiquidContainerComponent::getAcceptedHazards)
            .documentation(LiquidContainer.ACCEPTED_HAZARDS_DOCUMENTATION).add()
            .build();
    private final SingleLiquidContainer container;
    private final Map<UUID, LiquidContainerWindow> windows = new ConcurrentHashMap<>();

    public SimpleLiquidContainerComponent() {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, 10000, new String[0]);
    }

    public SimpleLiquidContainerComponent(LiquidStack content, long capacity, String[] supportedTags) {
        this.container = new SingleLiquidContainer(content, capacity, supportedTags);
    }

    public SimpleLiquidContainerComponent(long capacity, String[] supportedTags) {
        this.container = new SingleLiquidContainer(LiquidStack.EMPTY, capacity, supportedTags);
    }

    public static ComponentType<ChunkStore, SimpleLiquidContainerComponent> getComponentType() {
        return ComponentTypes.SIMPLE_LIQUID_CONTAINER;
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
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NonNullDecl BlockFace side, @NonNullDecl FluxAccess access) {
        if (container.matchesFluxType(type)) {
            return type.castHandler(container);
        }
        return null;
    }

    @NullableDecl
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<ChunkStore> clone() {
        return new SimpleLiquidContainerComponent(
                this.getContent().copy(),
                this.getCapacity(),
                this.getAcceptedHazards()
        );
    }

    public Map<UUID, LiquidContainerWindow> getActiveWindows() {
        return this.windows;
    }
}
