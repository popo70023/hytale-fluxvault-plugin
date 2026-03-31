/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy.container;

import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.energy.EnergyFlux;
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Collections;
import java.util.List;

public class SingleEnergyContainer extends EnergyContainer.FixedCapacity implements IFluxHandler<EnergyFlux> {
    private FluxEnergy content;

    public SingleEnergyContainer(FluxEnergy content, long capacity) {
        super(capacity);
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<FluxEnergy> getContents() {
        lock.readLock().lock();
        try {
            return Collections.singletonList(content);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public FluxEnergy getContent(int index) {
        lock.readLock().lock();
        try {
            return index == 0 ? content : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public FluxEnergy getContent() {
        return getContent(0);
    }

    @Override
    public void setContent(int index, FluxEnergy content) {
        lock.writeLock().lock();
        try {
            if (index == 0) {
                this.content = (content == null) ? FluxEnergy.of(0) : content;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setContent(FluxEnergy content) {
        setContent(0, content);
    }

    @Override
    public long getAllContentQuantity() {
        lock.readLock().lock();
        try {
            return content.getQuantity();
        } finally {
            lock.readLock().unlock();
        }
    }

    @NonNullDecl
    @Override
    public EnergyFlux fill(@NonNullDecl EnergyFlux resource, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            EnergyFlux resultFlux = new EnergyFlux();
            if (resource.isEmpty()) return resultFlux;

            FluxEnergy resourceStack = resource.getStack();
            long resourceQuantity = resourceStack.getQuantity();

            long currentQuantity = this.content.getQuantity();
            long spaceAvailable = getCapacity() - currentQuantity;
            if (spaceAvailable <= 0) return resultFlux;

            long canFill = Math.min(Math.min(spaceAvailable, resourceQuantity), resource.getTransferLimit());
            if (resource.isExact() && canFill < resourceQuantity) return resultFlux;

            if (action.execute()) {
                this.content.addQuantity(canFill);
                onContentsChanged();

                if (resourceStack.addQuantity(-canFill) == 0) resource.removeStack();
            }

            return resultFlux.addStack(FluxEnergy.of(canFill));
        } finally {
            activeLock.unlock();
        }
    }

    @NonNullDecl
    @Override
    public EnergyFlux drain(@NonNullDecl EnergyFlux requestResources, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            EnergyFlux resultFlux = new EnergyFlux();
            if (this.content.isEmpty() || requestResources.isEmpty()) return resultFlux;

            FluxEnergy requestStack = requestResources.getStack();
            long requestQuantity = requestStack.getQuantity();
            long limit = requestResources.getTransferLimit();

            long toDrain = Math.min(Math.min(requestQuantity, content.getQuantity()), limit);

            if (toDrain <= 0) return resultFlux;

            if (requestResources.isExact() && toDrain < requestQuantity) {
                return resultFlux;
            }

            if (action.execute()) {
                content.addQuantity(-toDrain);
                onContentsChanged();

                if (requestStack.addQuantity(-toDrain) == 0) {
                    requestResources.removeStack();
                }
            }

            return resultFlux.addStack(FluxEnergy.of(toDrain));
        } finally {
            activeLock.unlock();
        }
    }
}
