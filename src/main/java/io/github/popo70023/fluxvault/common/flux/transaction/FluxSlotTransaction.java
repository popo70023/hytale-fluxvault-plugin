/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux.transaction;

public record FluxSlotTransaction<D>(
        ActionType actionType,
        Short slotIndex,
        String contentId,
        long deltaAmount,
        D stateBefore,
        D stateAfter
) implements IFluxTransaction {
}
