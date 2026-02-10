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
    protected Set<String> supportedTags = Set.of();

    protected LiquidContainer(String capacityTypeStr, String[] supportedTags) {
        super(capacityTypeStr);
        this.setSupportedTags(supportedTags);
    }

    public Predicate<LiquidStack> getValidator() {
        return this::canAcceptLiquid;
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
