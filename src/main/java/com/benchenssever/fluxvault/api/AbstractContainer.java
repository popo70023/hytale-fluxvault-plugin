package com.benchenssever.fluxvault.api;

public abstract class AbstractContainer<D> implements IFluxContainer<D> {
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
        return this.capacityType.infiniteContent();
    }

    public boolean isInfiniteCapacity() {
        return this.capacityType.infiniteCapacity();
    }

    public void onContentsChanged() {
    }
}
