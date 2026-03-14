package com.benchenssever.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.AbstractContainer;
import com.benchenssever.fluxvault.energy.FluxEnergy;

public abstract class EnergyContainer extends AbstractContainer<FluxEnergy> {

    protected EnergyContainer(String capacityTypeStr) {
        super(capacityTypeStr);
    }

    @Override
    public int findFirstIndex() {
        for (int i = 0; i < getContainerMaxSize(); i++) {
            if (!getContent(i).isEmpty()) return i;
        }
        return -1;
    }

    @Override
    public int findIndexOfTarget(FluxEnergy target, boolean ignoreFull) {
        for (int i = 0; i < getContainerMaxSize(); i++) {
            FluxEnergy theContent = getContent(i);
            if (!ignoreFull || theContent.getQuantity() != getContainerCapacity()) return i;
        }
        return -1;
    }

    public abstract static class fixedCapacity extends EnergyContainer {
        protected long capacity;

        public fixedCapacity(String capacityTypeStr, long capacity) {
            super(capacityTypeStr);
            this.capacity = capacity;
        }

        public long getContainerCapacity() {
            return isInfiniteCapacity() ? Long.MAX_VALUE : capacity;
        }

        public void setContainerCapacity(long capacity) {
            this.capacity = capacity;
        }
    }
}
