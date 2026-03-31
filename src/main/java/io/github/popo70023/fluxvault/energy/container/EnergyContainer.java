/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy.container;

import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.energy.EnergyFlux;
import io.github.popo70023.fluxvault.energy.FluxEnergy;

public abstract class EnergyContainer extends AbstractContainer<FluxEnergy> {

    public static final String CONTENT_DOCUMENTATION = "The initial Flux Energy stored within the container upon creation.";

    @Override
    public int findFirstIndex() {
        lock.readLock().lock();
        try {
            for (int i = 0; i < getContainerMaxSize(); i++) {
                if (!getContent(i).isEmpty()) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int findIndexOfTarget(FluxEnergy target, boolean ignoreFull) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < getContainerMaxSize(); i++) {
                FluxEnergy theContent = getContent(i);
                if (!ignoreFull || theContent.getQuantity() != getCapacity()) return i;
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        return EnergyFlux.class.isAssignableFrom(fluxType.getResourceClass());
    }

    public abstract static class FixedCapacity extends EnergyContainer {
        protected volatile long capacity;

        public FixedCapacity(long capacity) {
            super();
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
