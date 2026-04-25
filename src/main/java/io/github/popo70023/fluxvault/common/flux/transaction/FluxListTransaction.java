/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux.transaction;

import java.util.List;

public class FluxListTransaction implements IFluxTransaction {
    private final List<? extends IFluxTransaction> subTransactions;

    public FluxListTransaction(List<? extends IFluxTransaction> subTransactions) {
        this.subTransactions = List.copyOf(subTransactions);
    }

    @Override
    public ActionType actionType() {
        return ActionType.MULTIPLE;
    }

    public List<? extends IFluxTransaction> getTransactions() {
        return subTransactions;
    }
}
