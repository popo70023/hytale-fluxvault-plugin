/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import io.github.popo70023.fluxvault.payload.liquid.container.SingleLiquidContainer;
import io.github.popo70023.fluxvault.payload.liquid.interaction.ui.LiquidContainerWindow;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SimpleLiquidContainerComponent<ECS_TYPE> implements Component<ECS_TYPE>, IFluxHandlerProvider, IFluxContainerProvider {
    private final Map<UUID, LiquidContainerWindow> windows = new ConcurrentHashMap<>();
    protected volatile long capacity = 10000;
    protected Set<String> acceptedHazards = Collections.emptySet();
    protected Set<String> whitelist = Collections.emptySet();

    protected transient volatile SingleLiquidContainer activeContainer;

    public SimpleLiquidContainerComponent() {
    }

    public SimpleLiquidContainerComponent(SimpleLiquidContainerComponent<ECS_TYPE> other) {
        this.capacity = other.capacity;
        this.acceptedHazards = new HashSet<>(other.acceptedHazards);
        this.whitelist = new HashSet<>(other.whitelist);

        if (other.activeContainer != null) {
            this.activeContainer = new SingleLiquidContainer(other.activeContainer);
        }
    }

    @Override
    public void sendContainerMessage(Player player) {
        LiquidStack content = getOrCreateContainer().getContent();
        if (content.isEmpty()) {
            Message message = Message.translation("server.fluxvault.interaction.container.empty")
                    .param("payloadName", Message.translation("server.fluxvault.Resource.Liquid.name"));
            player.sendMessage(message);
        } else {
            String liquidNameKey = content.getLiquid().getTranslationProperties().getName();
            Message message = Message.translation("server.fluxvault.interaction.container.contains")
                    .param("payloadName", liquidNameKey != null ? Message.translation(liquidNameKey) : Message.translation("server.fluxvault.Resource.Unnamed_Liquid.name"))
                    .param("payloadUnit", "mB")
                    .param("quantity", content.getQuantity());
            player.sendMessage(message);
        }
    }

    public SingleLiquidContainer getOrCreateContainer() {
        if (activeContainer == null) {
            synchronized (this) {
                if (activeContainer == null) {
                    activeContainer = new SingleLiquidContainer(capacity, acceptedHazards, whitelist, LiquidStack.EMPTY);
                }
            }
        }
        return activeContainer;
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NonNullDecl BlockFace face, String slotName, @NonNullDecl FluxAccess access) {
        SingleLiquidContainer targetContainer = getOrCreateContainer();
        if (targetContainer.matchesFluxType(type)) {
            return type.castHandler(targetContainer);
        }
        return null;
    }

    public Map<UUID, LiquidContainerWindow> getActiveWindows() {
        return this.windows;
    }

    @NullableDecl
    @Override
    public abstract Component<ECS_TYPE> clone();
}
