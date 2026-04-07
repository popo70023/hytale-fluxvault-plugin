/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import com.hypixel.hytale.server.core.entity.entities.Player;

/**
 * A provider interface that allows entities or blocks to expose their internal container states.
 * <p>
 * Implementing this interface indicates that the component contains resource containers
 * (e.g., fluid tanks, item inventories, energy buffers) that can be inspected by players.
 * It is primarily utilized to handle the player's "Inspect" interaction.
 * </p>
 */
public interface IFluxContainerProvider {

    /**
     * Sends a plain-text message detailing the current state of the containers to the player.
     * <p>
     * This method is invoked when a player triggers an inspection interaction
     * (e.g., right-clicking the block). Implementers should format the contents and
     * capacities of all internal containers into a readable system message and send it
     * to the specified player.
     * </p>
     *
     * @param player the player who triggered the interaction and will receive the message
     */
    void sendContainerMessage(Player player);
}
