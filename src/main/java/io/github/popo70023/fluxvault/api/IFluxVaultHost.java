/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.entity.entities.Player;

import java.util.function.Consumer;

/**
 * Represents a host (Entity or Block) that manages internal Flux containers.
 */
public interface IFluxVaultHost {

    /**
     * Sends a message detailing the current state of the containers to the specified player.
     *
     * @param player the player to receive the container information
     */
    void sendContainerMessage(Player player);

    /**
     * Registers a listener to be triggered whenever the internal state of the vault changes.
     *
     * @param consumer the callback to execute upon a state change
     */
    void registerChangeEvent(Consumer<? extends IEvent<Void>> consumer);

    /**
     * A specialized host interface for Blocks.
     */
    interface Block extends IFluxVaultHost {

        /**
         * Updates the host with the current block rotation.
         * <p>
         * Implementers can leave this as a default no-op if rotation data is not required.
         * </p>
         *
         * @param yaw the current rotation of the block
         */
        default void updateRotation(Rotation yaw) {
        }
    }
}
