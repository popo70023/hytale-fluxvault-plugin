/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.container;

import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;

public abstract class ResourceContainer extends AbstractContainer<ResourceStack> {
    protected String resourceId;
    private transient FluxType<?, ?> cachedFluxType;

    public ResourceContainer(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public int findFirstIndex() {
        lock.readLock().lock();
        try {
            for (int i = 0; i < getContainerMaxSize(); i++) {
                if (!ResourceStack.isEmpty(getContent(i))) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int findIndexOfTarget(ResourceStack target, boolean ignoreFull) {
        if (target == null || !this.resourceId.equals(target.getResourceId())) return -1;
        lock.readLock().lock();
        try {
            for (int i = 0; i < getContainerMaxSize(); i++) {
                ResourceStack theContent = getContent(i);
                long currentQty = (theContent == null) ? 0 : theContent.getQuantity();
                if (!ignoreFull || currentQty != getCapacity()) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        if (cachedFluxType == null) {
            cachedFluxType = FluxType.getResourceFluxTypeByName(resourceId);
        }
        return fluxType == cachedFluxType;
    }

    public abstract static class FixedCapacity extends ResourceContainer {
        protected volatile long capacity;

        public FixedCapacity(String resourceID, long capacity) {
            super(resourceID);
            this.capacity = capacity;
        }

        public long getCapacity() {
            return capacity;
        }

        public void setCapacity(long capacity) {
            lock.writeLock().lock();
            try {
                this.capacity = capacity;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
