/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux.transaction;

public record FluxDeltaTransaction(
        ActionType actionType,
        Short slotIndex,
        String contentId,
        long deltaQuantity,
        long quantityBefore,
        long quantityAfter
) implements IFluxTransaction {
}
