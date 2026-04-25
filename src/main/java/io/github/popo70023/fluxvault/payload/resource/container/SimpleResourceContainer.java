/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.container;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.common.flux.AbstractContainer;
import io.github.popo70023.fluxvault.common.flux.transaction.FluxDeltaTransaction;
import io.github.popo70023.fluxvault.common.flux.transaction.IFluxTransaction;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SimpleResourceContainer extends ResourceContainer.FixedCapacity implements IFluxHandler<ResourceFlux> {
    public static final BuilderCodec<SimpleResourceContainer> CODEC;
    private long storedQuantity = 0;

    public SimpleResourceContainer() {
        super(null, 0);
    }

    public SimpleResourceContainer(BluePrint bp) {
        super(bp.ResourceId, bp.capacity);
    }

    public SimpleResourceContainer(SimpleResourceContainer other) {
        super(other.getResourceId(), other.getCapacity());
        other.lock.readLock().lock();
        try {
            this.storedQuantity = other.storedQuantity;
        } finally {
            other.lock.readLock().unlock();
        }
    }

    @Override
    public short getContainerMaxSize() {
        return 1;
    }

    @Override
    public Short2ObjectMap<ResourceStack> getContents() {
        lock.readLock().lock();
        try {
            return Short2ObjectMaps.singleton((short) 0, ResourceStack.of(getResourceId(), storedQuantity));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ResourceStack getContent(short index) {
        lock.readLock().lock();
        try {
            return index == 0 ? ResourceStack.of(getResourceId(), storedQuantity) : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public ResourceStack getContent() {
        return getContent((short) 0);
    }

    @Override
    public void setContent(short index, ResourceStack content) {
        if (!getResourceId().equals(content.getResourceId())) return;
        lock.writeLock().lock();
        try {
            if (index == 0) this.storedQuantity = content.getQuantity();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setContent(ResourceStack content) {
        setContent((short) 0, content);
    }

    @Override
    public long getAllContentQuantity() {
        lock.readLock().lock();
        try {
            return storedQuantity;
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

            long currentQuantity = storedQuantity;
            long spaceAvailable = getCapacity() - currentQuantity;
            if (spaceAvailable <= 0) return resultFlux;

            long canFill = Math.min(Math.min(spaceAvailable, resourceQuantity), resource.getTransferLimit());
            if (resource.isExact() && canFill < resourceQuantity) return resultFlux;

            long stateBefore = storedQuantity;
            long stateAfter = currentQuantity + canFill;

            if (action.execute()) {
                this.storedQuantity += canFill;

                FluxDeltaTransaction transaction = new FluxDeltaTransaction(IFluxTransaction.ActionType.ADD, (short) 0, getResourceId(), canFill, stateBefore, stateAfter);
                onContentsChanged(transaction);

                if (resourceStack.addQuantity(-canFill) == 0) resource.removeStack();
            }

            return resultFlux.addStack(ResourceStack.of(getResourceId(), canFill));
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
            if (storedQuantity <= 0) return resultFlux;

            ResourceStack requestStack = requestResources.getStack();
            if (requestStack == null || requestStack.getQuantity() <= 0 || !this.resourceId.equals(requestStack.getResourceId()))
                return resultFlux;
            long requestQuantity = requestStack.getQuantity();
            long limit = requestResources.getTransferLimit();

            long toDrain = Math.min(Math.min(requestQuantity, storedQuantity), limit);

            if (toDrain <= 0) return resultFlux;

            if (requestResources.isExact() && toDrain < requestQuantity) return resultFlux;

            long stateBefore = storedQuantity;
            long stateAfter = storedQuantity - toDrain;

            if (action.execute()) {
                storedQuantity -= toDrain;
                FluxDeltaTransaction transaction = new FluxDeltaTransaction(IFluxTransaction.ActionType.REMOVE, (short) 0, getResourceId(), -toDrain, stateBefore, stateAfter);
                onContentsChanged(transaction);

                if (requestStack.addQuantity(-toDrain) == 0) {
                    requestResources.removeStack();
                }
            }

            return resultFlux.addStack(ResourceStack.of(getResourceId(), toDrain));
        } finally {
            activeLock.unlock();
        }
    }

    public static class BluePrint {
        private String ResourceId = "";
        private long capacity = 10000;

        public static final BuilderCodec<BluePrint> CODEC = BuilderCodec.builder(BluePrint.class, BluePrint::new)
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (bp, v) -> bp.ResourceId = v, BluePrint::getResourceId)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), (bp, v) -> bp.capacity = v, BluePrint::getCapacity)
                .add()
                .build();

        public String getResourceId() {
            return ResourceId;
        }

        public long getCapacity() {
            return capacity;
        }

        public BluePrint copy() {
            BluePrint copy = new BluePrint();

            copy.ResourceId = this.ResourceId;
            copy.capacity = this.capacity;

            return copy;
        }
    }

    static {
        CODEC = BuilderCodec.builder(SimpleResourceContainer.class, SimpleResourceContainer::new)
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (container, v) -> container.resourceId = v, SimpleResourceContainer::getResourceId)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SimpleResourceContainer::setCapacity, SimpleResourceContainer::getCapacity)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.CONTENT_KEY, Codec.LONG), (container, v) -> container.storedQuantity = v, (container) -> container.storedQuantity)
                .add()
                .build();
    }
}
