/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux.transaction;

public interface IFluxTransaction {

    ActionType actionType();

    enum ActionType {
        ADD,
        REMOVE,
        REPLACE,
        CLEAR,
        MULTIPLE;

        public boolean add() {
            return this == ADD;
        }

        public boolean remove() {
            return this == REMOVE;
        }
    }
}
