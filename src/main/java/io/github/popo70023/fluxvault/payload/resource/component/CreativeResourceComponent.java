/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.BlockFace;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;
import io.github.popo70023.fluxvault.payload.resource.container.CreativeResourceHandler;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CreativeResourceComponent<ECS_TYPE> implements Component<ECS_TYPE>, IFluxHandlerProvider<BlockFace> {
    protected Set<String> supportedResources = Collections.emptySet();
    protected boolean isVoid = false;
    private transient volatile Map<String, CreativeResourceHandler> handlerCache;

    public CreativeResourceComponent() {
    }

    public CreativeResourceComponent(CreativeResourceComponent<ECS_TYPE> other) {
        this.supportedResources = new HashSet<>(other.supportedResources);
        this.isVoid = other.isVoid;
        this.handlerCache = null;
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NonNullDecl BlockFace face, String slotName, @NonNullDecl FluxAccess access) {
        String requestedId = type.getName();

        if (!this.supportedResources.contains(requestedId)) return null;
        if (this.handlerCache == null) this.handlerCache = new ConcurrentHashMap<>();

        if (this.handlerCache == null) {
            synchronized (this) {
                if (this.handlerCache == null) {
                    this.handlerCache = new ConcurrentHashMap<>();
                }
            }
        }

        CreativeResourceHandler handler = this.handlerCache.computeIfAbsent(requestedId, k ->
                new CreativeResourceHandler(k, this.isVoid)
        );

        if ((this.isVoid && access.fill()) || (!this.isVoid && access.drain())) {
            return type.castHandler(handler);
        }

        return null;
    }

    @NullableDecl
    @Override
    public abstract Component<ECS_TYPE> clone();
}
