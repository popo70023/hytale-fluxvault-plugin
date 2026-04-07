/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.container;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SingleResourceContainer extends ResourceContainer.FixedCapacity implements IFluxHandler<ResourceFlux> {
    public static final BuilderCodec<SingleResourceContainer> CODEC;
    private ResourceStack content;

    public SingleResourceContainer() {
        super(null, 0);
        this.content = null;
    }

    public SingleResourceContainer(String resourceID, long capacity, ResourceStack content) {
        super(resourceID, capacity);
        this.content = content;
    }

    public SingleResourceContainer(SingleResourceContainer other) {
        super(other.getResourceId(), other.getCapacity());
        other.lock.readLock().lock();
        try {
            this.content = other.content != null ? other.content.copy() : null;
        } finally {
            other.lock.readLock().unlock();
        }
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<ResourceStack> getContents() {
        lock.readLock().lock();
        try {
            return content != null ? List.of(content.copy()) : List.of();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ResourceStack getContent(int index) {
        lock.readLock().lock();
        try {
            return (index == 0 && content != null) ? content.copy() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public ResourceStack getContent() {
        return getContent(0);
    }

    @Override
    public void setContent(int index, ResourceStack content) {
        if (!getResourceId().equals(content.getResourceId())) return;
        lock.writeLock().lock();
        try {
            if (index == 0) {
                this.content = (content != null) ? content.copy() : null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setContent(ResourceStack content) {
        setContent(0, content);
    }

    @Override
    public long getAllContentQuantity() {
        lock.readLock().lock();
        try {
            return content != null ? content.getQuantity() : 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    @NonNullDecl
    @Override
    public ResourceFlux fill(@NonNullDecl ResourceFlux resource, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            ResourceFlux resultFlux = new ResourceFlux();
            if (resource.isEmpty()) return resultFlux;

            ResourceStack resourceStack = resource.getStack();
            if (!this.resourceId.equals(resourceStack.getResourceId())) return resultFlux;
            long resourceQuantity = resourceStack.getQuantity();

            long currentQuantity = (this.content != null) ? this.content.getQuantity() : 0;
            long spaceAvailable = getCapacity() - currentQuantity;
            if (spaceAvailable <= 0) return resultFlux;

            long canFill = Math.min(Math.min(spaceAvailable, resourceQuantity), resource.getTransferLimit());
            if (resource.isExact() && canFill < resourceQuantity) return resultFlux;

            if (action.execute()) {
                if (this.content == null) {
                    this.content = ResourceStack.of(this.resourceId, canFill);
                } else {
                    this.content.addQuantity(canFill);
                }
                onContentsChanged();

                if (resourceStack.addQuantity(-canFill) == 0) resource.removeStack();
            }

            return resultFlux.addStack(ResourceStack.of(content.getResourceId(), canFill));
        } finally {
            activeLock.unlock();
        }
    }

    @NonNullDecl
    @Override
    public ResourceFlux drain(@NonNullDecl ResourceFlux requestResources, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            ResourceFlux resultFlux = new ResourceFlux();
            if (this.content == null || this.content.isEmpty() || requestResources.isEmpty()) return resultFlux;

            ResourceStack requestStack = requestResources.getStack();
            if (requestStack != null && !this.resourceId.equals(requestStack.getResourceId())) return resultFlux;
            long requestQuantity = requestStack.getQuantity();
            long limit = requestResources.getTransferLimit();

            long toDrain = Math.min(Math.min(requestQuantity, content.getQuantity()), limit);

            if (toDrain <= 0) return resultFlux;

            if (requestResources.isExact() && toDrain < requestQuantity) return resultFlux;

            if (action.execute()) {
                content.addQuantity(-toDrain);
                onContentsChanged();

                if (requestStack.addQuantity(-toDrain) == 0) {
                    requestResources.removeStack();
                }
            }

            return resultFlux.addStack(ResourceStack.of(content.getResourceId(), toDrain));
        } finally {
            activeLock.unlock();
        }
    }

    static {
        CODEC = BuilderCodec.builder(SingleResourceContainer.class, SingleResourceContainer::new)
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (container, v) -> {
                    if (container.getContent() != null) {
                        container.resourceId = container.getContent().getResourceId();
                    } else {
                        container.resourceId = v;
                    }
                }, SingleResourceContainer::getResourceId)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SingleResourceContainer::setCapacity, SingleResourceContainer::getCapacity)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, ResourceStack.CODEC), (container, stack) -> container.setContent(stack), SingleResourceContainer::getContent)
                .add()
                .build();
    }
}
