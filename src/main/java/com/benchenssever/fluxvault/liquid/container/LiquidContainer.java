package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.IFluxContainer;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public abstract class LiquidContainer implements IFluxContainer<LiquidFlux, LiquidStack> {
    protected long capacity;
    protected CapacityType capacityType;
    protected Set<String> supportedTags = Set.of();

    protected LiquidContainer(long capacity, String capacityTypeStr, String[] supportedTags) {
        this.capacity = capacity;
        try {
            this.capacityType = CapacityType.valueOf(capacityTypeStr);
        } catch (IllegalArgumentException | NullPointerException e) {
            this.capacityType = CapacityType.FINITE;
        }
        this.setSupportedTags(supportedTags);
    }

    public long getContainerCapacity() {
        return this.capacityType == CapacityType.INFINITE_CAPACITY ? Long.MAX_VALUE : capacity;
    }

    public void setContainerCapacity(long capacity) {
        this.capacity = capacity;
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

    public Predicate<LiquidStack> getValidator() {
        return this::canAcceptLiquid;
    }

    public boolean isInfiniteContent() {
        return this.capacityType == CapacityType.INFINITE_CONTENT;
    }

    public boolean isInfiniteCapacity() {
        return this.capacityType == CapacityType.INFINITE_CAPACITY;
    }

    public String[] getSupportedTags() {
        return this.supportedTags.toArray(String[]::new);
    }

    public void setSupportedTags(String[] supportedTags) {
        if (supportedTags == null) {
            this.supportedTags = Collections.emptySet();
            return;
        }
        this.supportedTags = Set.copyOf(new HashSet<>(Arrays.asList(supportedTags)));
    }

    public boolean canAcceptLiquid(LiquidStack resource) {
        if (resource.isEmpty()) return false;

        Set<String> liquidTags = resource.getLiquid().tags();
        if (liquidTags.isEmpty()) return true;

        return this.supportedTags.containsAll(liquidTags);
    }

    public void onContentsChanged() {
    }

    public enum CapacityType {
        FINITE,
        INFINITE_CAPACITY,
        INFINITE_CONTENT
    }
}
