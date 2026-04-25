/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.component;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.popo70023.fluxvault.common.flux.AbstractVault;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import io.github.popo70023.fluxvault.payload.liquid.container.SimpleLiquidContainer;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleLiquidContainerVault extends AbstractVault {
    protected volatile Map<String, SimpleLiquidContainer.BluePrint> containerBluePrints = new HashMap<>();
    protected volatile Map<String, SimpleLiquidContainer> activeContainers = new ConcurrentHashMap<>();

    public SimpleLiquidContainerVault() {
        super();
    }

    public SimpleLiquidContainerVault(SimpleLiquidContainerVault other) {
        super();

        if (other.containerBluePrints != null) {
            for (Map.Entry<String, SimpleLiquidContainer.BluePrint> entry : other.containerBluePrints.entrySet()) {
                this.containerBluePrints.put(entry.getKey(), entry.getValue().copy());
            }
        } else {
            this.containerBluePrints = new HashMap<>();
        }

        if (other.activeContainers != null) {
            for (Map.Entry<String, SimpleLiquidContainer> entry : other.activeContainers.entrySet()) {
                SimpleLiquidContainer copiedContainer = new SimpleLiquidContainer(entry.getValue());
                this.attachNerve(copiedContainer);
                this.activeContainers.put(entry.getKey(), copiedContainer);
            }
        }
    }

    public void attachNerve(SimpleLiquidContainer container) {
        if (container == null) return;
        container.clearChangeListeners();
        container.addChangeListener(tx -> {
            ChangeEvent event = new ChangeEvent(this, tx);
            this.internalChangeEventRegistry.dispatchFor(null).dispatch(event);
        });
    }

    public void rebindAllNerves() {
        if (this.activeContainers != null) {
            for (SimpleLiquidContainer container : this.activeContainers.values()) {
                this.attachNerve(container);
            }
        }
    }

    public void sendContainerMessage(Player player) {
        if (this.activeContainers == null || this.activeContainers.isEmpty()) return;

        for (String containerName : activeContainers.keySet()) {
            SimpleLiquidContainer container = activeContainers.get(containerName);
            Short2ObjectMap<LiquidStack> contents = container.getContents();
            for (Short index = 0; index < container.getContainerMaxSize(); index++) {
                LiquidStack content = contents.get(index);
                if (LiquidStack.isEmpty(content)) {
                    Message message = Message.translation("server.fluxvault.interaction.container.slot_empty")
                            .param("containerName", containerName)
                            .param("index", index);
                    player.sendMessage(message);
                } else {
                    String liquidNameKey = content.getLiquid().getTranslationProperties().getName();
                    Message message = Message.translation("server.fluxvault.interaction.container.slot_contain")
                            .param("containerName", containerName)
                            .param("index", index)
                            .param("payloadName", liquidNameKey != null ? Message.translation(liquidNameKey) : Message.translation("server.fluxvault.Resource.Unnamed_Liquid.name"))
                            .param("payloadUnit", "mB")
                            .param("quantity", content.getQuantity());
                    player.sendMessage(message);
                }

            }
        }
    }

    public SimpleLiquidContainer getOrCreateContainer(String containerName) {
        if (containerName == null || this.containerBluePrints == null) return null;
        SimpleLiquidContainer.BluePrint bp = containerBluePrints.get(containerName);
        if (bp == null) return null;
        if (this.activeContainers == null) {
            this.activeContainers = new ConcurrentHashMap<>();
        }
        return this.activeContainers.computeIfAbsent(containerName, _ -> {
            SimpleLiquidContainer newContainer = new SimpleLiquidContainer(bp);
            newContainer.addChangeListener(tx -> {
                ChangeEvent event = new ChangeEvent(this, tx);
                this.internalChangeEventRegistry.dispatchFor(null).dispatch(event);
            });
            return newContainer;
        });
    }
}
