/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.popo70023.fluxvault.common.flux.AbstractVault;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import io.github.popo70023.fluxvault.payload.resource.container.SimpleResourceContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleResourceContainerVault extends AbstractVault {
    protected volatile Map<String, SimpleResourceContainer.BluePrint> containerBluePrints = new HashMap<>();
    protected transient volatile Map<String, SimpleResourceContainer> activeContainers = new ConcurrentHashMap<>();

    public SimpleResourceContainerVault() {
        super();
    }

    public SimpleResourceContainerVault(SimpleResourceContainerVault other) {
        super();

        if (other.containerBluePrints != null) {
            for (Map.Entry<String, SimpleResourceContainer.BluePrint> entry : other.containerBluePrints.entrySet()) {
                this.containerBluePrints.put(entry.getKey(), entry.getValue().copy());
            }
        } else {
            this.containerBluePrints = new HashMap<>();
        }

        for (Map.Entry<String, SimpleResourceContainer> entry : other.activeContainers.entrySet()) {
            this.activeContainers.put(entry.getKey(), new SimpleResourceContainer(entry.getValue()));
        }
    }

    public void attachNerve(SimpleResourceContainer container) {
        if (container == null) return;
        container.clearChangeListeners();
        container.addChangeListener(tx -> {
            ChangeEvent event = new ChangeEvent(this, tx);
            this.internalChangeEventRegistry.dispatchFor(null).dispatch(event);
        });
    }

    public void rebindAllNerves() {
        if (this.activeContainers != null) {
            for (SimpleResourceContainer container : this.activeContainers.values()) {
                this.attachNerve(container);
            }
        }
    }

    public void sendContainerMessage(Player player) {
        for (String containerName : containerBluePrints.keySet()) {
            SimpleResourceContainer container = getOrCreateContainer(containerName);
            String targetResourceId = container.getResourceId();
            if (container == null) {
                Message message = Message.translation("server.fluxvault.interaction.container.not_found")
                        .param("containerName", containerName)
                        .param("payloadName", targetResourceId);
                player.sendMessage(message);
                return;
            }
            ResourceStack content = container.getContent();
            FluxResource fluxResource = FluxResource.getResourceById(content.getResourceId());
            String translationKey = fluxResource.getTranslationProperties().getName();
            if (content.isEmpty()) {
                Message message = Message.translation("server.fluxvault.interaction.container.empty")
                        .param("containerName", containerName)
                        .param("payloadName", translationKey != null ? Message.translation(translationKey) : Message.translation("server.fluxvault.Resource.Unnamed_Resource.name"));
                player.sendMessage(message);
            } else {
                Message message = Message.translation("server.fluxvault.interaction.container.contains")
                        .param("containerName", containerName)
                        .param("payloadName", translationKey != null ? Message.translation(translationKey) : Message.translation("server.fluxvault.Resource.Unnamed_Resource.name"))
                        .param("payloadUnit", fluxResource.getResourceUnit())
                        .param("quantity", content.getQuantity());
                player.sendMessage(message);
            }
        }
    }

    public SimpleResourceContainer getOrCreateContainer(String containerName) {
        SimpleResourceContainer.BluePrint bp = this.containerBluePrints.get(containerName);
        if (bp == null) return null;
        return this.activeContainers.computeIfAbsent(containerName, _ -> new SimpleResourceContainer(bp));
    }
}
