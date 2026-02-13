package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.AbstractContainer;
import com.benchenssever.fluxvault.api.CapacityType;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public abstract class LiquidContainer extends AbstractContainer<LiquidFlux, LiquidStack> {
    protected Set<String> acceptedHazards = Set.of();

    protected LiquidContainer(String capacityTypeStr, String[] acceptedHazards) {
        super(capacityTypeStr);
        this.setAcceptedHazards(acceptedHazards);
    }

    public Predicate<LiquidStack> getValidator() {
        return this::canAcceptLiquid;
    }

    public String[] getAcceptedHazards() {
        return this.acceptedHazards.toArray(String[]::new);
    }

    public void setAcceptedHazards(String[] acceptedHazards) {
        if (acceptedHazards == null) {
            this.acceptedHazards = Collections.emptySet();
            return;
        }
        this.acceptedHazards = Set.copyOf(new HashSet<>(Arrays.asList(acceptedHazards)));
    }

    public boolean canAcceptLiquid(LiquidStack resource) {
        if (resource.isEmpty()) return false;

        Set<String> liquidTags = resource.getLiquid().getHazards();
        if (liquidTags.isEmpty()) return true;

        return this.acceptedHazards.containsAll(liquidTags);
    }

    public abstract static class fixedCapacity extends LiquidContainer {
        protected long capacity;

        public fixedCapacity(long capacity, String capacityTypeStr, String[] supportedTags) {
            super(capacityTypeStr, supportedTags);
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
