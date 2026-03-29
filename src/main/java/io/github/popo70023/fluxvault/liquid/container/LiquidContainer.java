package io.github.popo70023.fluxvault.liquid.container;

import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.liquid.LiquidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public abstract class LiquidContainer extends AbstractContainer<LiquidStack> {
    public static final String CONTENT_DOCUMENTATION = "The initial liquid stack stored within the container upon creation.";
    public static final String ACCEPTED_HAZARDS_KEY = "AcceptedHazards";
    public static final String ACCEPTED_HAZARDS_DOCUMENTATION = "A whitelist of liquid hazard types (e.g., Molten, Corrosive) that this container accepts. Liquids with unlisted hazards will be rejected.";

    protected volatile Set<String> acceptedHazards = Set.of();

    protected LiquidContainer(String[] acceptedHazards) {
        super();
        this.setAcceptedHazards(acceptedHazards);
    }

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
    public int findIndexOfTarget(LiquidStack target, boolean ignoreFull) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < getContainerMaxSize(); i++) {
                LiquidStack theContent = getContent(i);
                if (theContent.isEquivalentType(target)) {
                    if (!ignoreFull || theContent.getQuantity() != getCapacity()) return i;
                }
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Predicate<LiquidStack> getValidator() {
        return this::canAcceptLiquid;
    }

    public boolean canAcceptLiquid(LiquidStack resource) {
        if (resource.isEmpty()) return false;

        Set<String> liquidTags = resource.getLiquid().getHazards();
        if (liquidTags.isEmpty()) return true;

        return this.acceptedHazards.containsAll(liquidTags);
    }

    public String[] getAcceptedHazards() {
        return this.acceptedHazards.toArray(String[]::new);
    }

    public void setAcceptedHazards(String[] acceptedHazards) {
        lock.writeLock().lock();
        try {
            if (acceptedHazards == null) {
                this.acceptedHazards = Collections.emptySet();
                return;
            }
            this.acceptedHazards = Set.copyOf(new HashSet<>(Arrays.asList(acceptedHazards)));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public abstract static class FixedCapacity extends LiquidContainer {
        protected volatile long capacity;

        protected FixedCapacity(long capacity, String[] supportedTags) {
            super(supportedTags);
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
