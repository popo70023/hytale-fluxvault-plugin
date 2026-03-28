package com.benchenssever.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.AbstractContainer;
import com.benchenssever.fluxvault.energy.FluxEnergy;

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
