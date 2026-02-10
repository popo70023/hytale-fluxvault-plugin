package com.benchenssever.fluxvault.api;

public abstract class AbstractContainer<T extends IFlux<T, D>, D> implements IFluxContainer<T, D> {
    protected CapacityType capacityType;

    protected AbstractContainer(String capacityTypeStr) {
        try {
            this.capacityType = CapacityType.valueOf(capacityTypeStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            this.capacityType = CapacityType.FINITE;
        }
    }

    public String getCapacityType() {
        return this.capacityType.name();
    }

    public void setCapacityType(String capacityTypeStr) {
        try {
            this.capacityType = CapacityType.valueOf(capacityTypeStr);
        } catch (Exception e) {
            this.capacityType = CapacityType.FINITE;
        }
    }

    public boolean isInfiniteContent() {
        return this.capacityType == CapacityType.INFINITE_CONTENT;
    }

    public boolean isInfiniteCapacity() {
        return this.capacityType == CapacityType.INFINITE_CAPACITY;
    }

    public void onContentsChanged() {
    }


    public abstract static class fixedCapacity<T extends IFlux<T, D>, D> extends AbstractContainer<T, D> {
        protected long capacity;

        public fixedCapacity(long capacity, String capacityTypeStr) {
            super(capacityTypeStr);
            this.capacity = capacity;
        }

        public long getContainerCapacity() {
            return this.capacityType == CapacityType.INFINITE_CAPACITY ? Long.MAX_VALUE : capacity;
        }

        public void setContainerCapacity(long capacity) {
            this.capacity = capacity;
        }
    }
}
