/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import io.github.popo70023.fluxvault.payload.resource.container.SingleResourceContainer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SimpleResourceContainerComponent<ECS_TYPE> implements Component<ECS_TYPE>, IFluxHandlerProvider, IFluxContainerProvider {
    protected Map<String, Long> capacities = new ConcurrentHashMap<>();
    protected transient volatile Map<String, SingleResourceContainer> activeContainers = new ConcurrentHashMap<>();

    public SimpleResourceContainerComponent() {
    }

    public SimpleResourceContainerComponent(SimpleResourceContainerComponent<ECS_TYPE> other) {
        this.capacities = new ConcurrentHashMap<>(other.capacities);

        for (Map.Entry<String, SingleResourceContainer> entry : other.activeContainers.entrySet()) {
            this.activeContainers.put(entry.getKey(), new SingleResourceContainer(entry.getValue()));
        }
    }

    @Override
    public void sendContainerMessage(Player player) {
        for (String targetResourceId : capacities.keySet()) {
            SingleResourceContainer container = getOrCreateContainer(targetResourceId);
            if (container == null) {
                Message message = Message.translation("server.fluxvault.interaction.container.notfonud")
                        .param("payloadName", targetResourceId);
                player.sendMessage(message);
                return;
            }
            ResourceStack content = container.getContent();
            FluxResource fluxResource = FluxResource.getResourceById(content.getResourceId());
            String translationKey = fluxResource.getTranslationProperties().getName();
            if (content.isEmpty()) {
                Message message = Message.translation("server.fluxvault.interaction.container.empty")
                        .param("payloadName", translationKey != null ? Message.translation(translationKey) : Message.translation("server.fluxvault.Resource.Unnamed_Resource.name"));
                player.sendMessage(message);
            } else {
                Message message = Message.translation("server.fluxvault.interaction.container.contains")
                        .param("payloadName", translationKey != null ? Message.translation(translationKey) : Message.translation("server.fluxvault.Resource.Unnamed_Resource.name"))
                        .param("payloadUnit", fluxResource.getResourceUnit())
                        .param("quantity", content.getQuantity());
                player.sendMessage(message);
            }
        }
    }

    public SingleResourceContainer getOrCreateContainer(String resourceId) {
        Long maxCapacity = this.capacities.get(resourceId);
        if (maxCapacity == null) return null;
        return this.activeContainers.computeIfAbsent(resourceId, id -> new SingleResourceContainer(id, maxCapacity, ResourceStack.of(id, 0)));
    }

    @NullableDecl
    @Override
    public <T extends IFlux<D>, D> IFluxHandler<T> getFluxHandler(@NonNullDecl FluxType<T, D> type, @NonNullDecl BlockFace face, String slotName, @NonNullDecl FluxAccess access) {
        SingleResourceContainer target = getOrCreateContainer(type.getName());
        if (target != null && target.matchesFluxType(type)) {
            return type.castHandler(target);
        }
        return null;
    }

    @NullableDecl
    @Override
    public abstract Component<ECS_TYPE> clone();
}
