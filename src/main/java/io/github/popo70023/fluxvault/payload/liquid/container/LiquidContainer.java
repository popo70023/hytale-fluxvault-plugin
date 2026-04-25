/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.container;

import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.common.flux.AbstractContainer;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public abstract class LiquidContainer extends AbstractContainer<LiquidStack> {
    public static final String ACCEPTED_HAZARDS_KEY = "AcceptedHazards";
    public static final String ACCEPTED_HAZARDS_DOCUMENTATION = "A whitelist of liquid hazard types (e.g., Molten, Corrosive) that this container accepts. Liquids with unlisted hazards will be rejected.";
    public static final String WHITELIST_KEY = "Whitelist";
    public static final String WHITELIST_DOCUMENTATION = "A list of specific Liquid IDs this container can accept. If empty, it accepts all liquids (subject to hazard checks).";

    protected volatile Set<String> acceptedHazards;
    protected volatile Set<String> whitelist;

    protected LiquidContainer(Set<String> acceptedHazards, Set<String> whitelist) {
        super();
        this.setAcceptedHazards(acceptedHazards);
        this.setWhitelist(whitelist);
    }

    public Predicate<LiquidStack> getValidator() {
        return this::canAcceptLiquid;
    }

    public boolean canAcceptLiquid(LiquidStack resource) {
        if (resource.isEmpty()) return false;

        if (!this.whitelist.isEmpty() && !this.whitelist.contains(resource.getLiquidId())) {
            return false;
        }

        Set<String> liquidTags = resource.getLiquid().getHazards();
        if (liquidTags.isEmpty()) return true;

        return this.acceptedHazards.containsAll(liquidTags);
    }

    public Set<String> getAcceptedHazards() {
        lock.readLock().lock();
        try {
            return Set.copyOf(this.acceptedHazards);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setAcceptedHazards(Set<String> acceptedHazards) {
        lock.writeLock().lock();
        try {
            if (acceptedHazards == null) {
                this.acceptedHazards = Collections.emptySet();
            } else {
                this.acceptedHazards = Set.copyOf(acceptedHazards);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Set<String> getWhitelist() {
        lock.readLock().lock();
        try {
            return Set.copyOf(this.whitelist);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void setWhitelist(Set<String> whitelist) {
        lock.writeLock().lock();
        try {
            if (whitelist == null) {
                this.whitelist = Collections.emptySet();
            } else {
                this.whitelist = Set.copyOf(whitelist);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        return fluxType == FluxType.LIQUID;
    }

    public abstract static class FixedCapacity extends LiquidContainer {
        protected volatile long capacity;

        protected FixedCapacity(Set<String> supportedTags, Set<String> whitelist, long capacity) {
            super(supportedTags, whitelist);
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
