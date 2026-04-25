/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.registry;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FluxVaultSystemRegistry {

    private FluxVaultSystemRegistry() {
    }

    private static final Set<ComponentType<ChunkStore, ? extends Component<ChunkStore>>> VAULT_BLOCK_COMPONENT_TYPE = new HashSet<>();

    public static <T extends Component<ChunkStore>> ComponentType<ChunkStore, T> registerVaultComponent(ComponentType<ChunkStore, T> type) {
        if (type != null) {
            VAULT_BLOCK_COMPONENT_TYPE.add(type);
        }
        return type;
    }

    public static Set<ComponentType<ChunkStore, ? extends Component<ChunkStore>>> getRegisteredComponent() {
        return Collections.unmodifiableSet(VAULT_BLOCK_COMPONENT_TYPE);
    }
}
