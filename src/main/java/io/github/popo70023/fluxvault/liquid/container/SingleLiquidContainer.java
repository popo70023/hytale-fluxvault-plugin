/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.liquid.container;

import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.liquid.Liquid;
import io.github.popo70023.fluxvault.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SingleLiquidContainer extends LiquidContainer.FixedCapacity implements IFluxHandler<LiquidFlux> {
    private LiquidStack content;

    public SingleLiquidContainer(LiquidStack content, long capacity, String[] acceptedHazards) {
        super(capacity, acceptedHazards);
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<LiquidStack> getContents() {
        lock.readLock().lock();
        try {
            return List.of(this.content);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public LiquidStack getContent(int index) {
        lock.readLock().lock();
        try {
            return index == 0 ? content : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LiquidStack getContent() {
        return getContent(0);
    }

    @Override
    public void setContent(int index, LiquidStack content) {
        lock.writeLock().lock();
        try {
            if (index == 0) {
                this.content = (content == null) ? LiquidStack.EMPTY : content;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setContent(LiquidStack content) {
        setContent(0, content);
    }

    @Override
    public long getAllContentQuantity() {
        lock.readLock().lock();
        try {
            return this.content.getQuantity();
        } finally {
            lock.readLock().unlock();
        }
    }

    @NonNullDecl
    @Override
    public LiquidFlux fill(@NonNullDecl LiquidFlux resource, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            LiquidFlux resultFlux = new LiquidFlux();

            boolean currentlyEmpty = this.content.isEmpty();

            if (resource.isEmpty()) return resultFlux;

            long maxCapacity = getCapacity();
            long currentQty = this.content.getQuantity();
            long spaceAvailable = maxCapacity - currentQty;

            if (spaceAvailable <= 0) return resultFlux;

            long simSpace = spaceAvailable;
            long simLimit = resource.getTransferLimit();
            long totalCalculatedFill = 0;

            String acceptedLiquidId = currentlyEmpty ? null : this.content.getLiquidId();

            for (int i = 0; i < resource.getStackCount(); i++) {
                if (simSpace <= 0 || simLimit <= 0) break;

                LiquidStack reqStack = resource.getStack(i);

                if (acceptedLiquidId == null) {
                    if (getValidator().test(reqStack)) {
                        acceptedLiquidId = reqStack.getLiquidId();
                    } else {
                        continue;
                    }
                }

                if (reqStack.getLiquidId().equals(acceptedLiquidId)) {
                    long toFill = Math.min(Math.min(reqStack.getQuantity(), simSpace), simLimit);
                    if (toFill > 0) {
                        totalCalculatedFill += toFill;
                        simSpace -= toFill;
                        simLimit -= toFill;
                    }
                }
            }

            if (totalCalculatedFill <= 0) return resultFlux;

            if (action.exact()) {
                long targetExact = Math.min(resource.getAllQuantity(), resource.getTransferLimit());
                if (totalCalculatedFill < targetExact) {
                    return resultFlux;
                }
            }

            if (action.simulate()) {
                return resultFlux.addStack(LiquidStack.of(acceptedLiquidId, totalCalculatedFill));
            }

            long totalActualFilled = 0;
            long execSpace = spaceAvailable;
            long execLimit = resource.getTransferLimit();

            for (int i = resource.getStackCount() - 1; i >= 0; i--) {
                if (execSpace <= 0 || execLimit <= 0) break;

                LiquidStack reqStack = resource.getStack(i);
                if (reqStack.getLiquidId().equals(acceptedLiquidId)) {
                    long toFill = Math.min(Math.min(reqStack.getQuantity(), execSpace), execLimit);
                    if (toFill <= 0) continue;

                    totalActualFilled += toFill;
                    execSpace -= toFill;
                    execLimit -= toFill;

                    if (reqStack.addQuantity(-toFill) == 0) {
                        resource.removeStack(i);
                    }
                }
            }

            if (currentlyEmpty) {
                this.content = LiquidStack.of(acceptedLiquidId, totalActualFilled);
            } else {
                this.content.addQuantity(totalActualFilled);
            }
            onContentsChanged();

            return resultFlux.addStack(LiquidStack.of(acceptedLiquidId, totalActualFilled));

        } finally {
            activeLock.unlock();
        }
    }

    @NonNullDecl
    @Override
    public LiquidFlux drain(@NonNullDecl LiquidFlux requestResources, @NonNullDecl FluxAction action) {
        java.util.concurrent.locks.Lock activeLock = getActiveLock(action);
        activeLock.lock();
        try {
            LiquidFlux resultFlux = new LiquidFlux();
            if (this.content.isEmpty() || requestResources.isEmpty()) return resultFlux;
            if (!requestResources.matchesWithFlux(this.content)) return resultFlux;

            String contentLiquidId = this.content.getLiquidId();
            long availableQuantity = this.content.getQuantity();
            long remainingLimit = requestResources.getTransferLimit();

            long totalCalculatedDrain = 0;
            long simAvailable = availableQuantity;
            long simLimit = remainingLimit;

            for (int pass = 0; pass < 2; pass++) {
                if (simAvailable <= 0 || simLimit <= 0) break;
                String targetId = (pass == 0) ? contentLiquidId : Liquid.EMPTY_ID;
                for (int i = 0; i < requestResources.getStackCount(); i++) {
                    if (simAvailable <= 0 || simLimit <= 0) break;
                    LiquidStack reqStack = requestResources.getStack(i);
                    if (reqStack.getLiquidId().equals(targetId)) {
                        long toDrain = Math.min(Math.min(reqStack.getQuantity(), simAvailable), simLimit);
                        totalCalculatedDrain += toDrain;
                        simAvailable -= toDrain;
                        simLimit -= toDrain;
                    }
                }
            }

            if (totalCalculatedDrain <= 0) return resultFlux;

            if (action.exact()) {
                long targetExact = Math.min(requestResources.getAllQuantity(), requestResources.getTransferLimit());
                if (totalCalculatedDrain < targetExact) {
                    return resultFlux;
                }
            }

            if (action.simulate()) {
                return resultFlux.addStack(LiquidStack.of(contentLiquidId, totalCalculatedDrain));
            }

            long totalActualDrained = 0;

            for (int pass = 0; pass < 2; pass++) {
                if (availableQuantity <= 0 || remainingLimit <= 0) break;
                String targetId = (pass == 0) ? contentLiquidId : Liquid.EMPTY_ID;
                for (int i = requestResources.getStackCount() - 1; i >= 0; i--) {
                    if (availableQuantity <= 0 || remainingLimit <= 0) break;

                    LiquidStack reqStack = requestResources.getStack(i);
                    if (reqStack.getLiquidId().equals(targetId)) {
                        long toDrain = Math.min(Math.min(reqStack.getQuantity(), availableQuantity), remainingLimit);
                        if (toDrain <= 0) continue;

                        totalActualDrained += toDrain;
                        availableQuantity -= toDrain;
                        remainingLimit -= toDrain;

                        if (targetId.equals(Liquid.EMPTY_ID)) {
                            reqStack = requestResources.setStack(i, LiquidStack.of(contentLiquidId, reqStack.getQuantity()));
                        }
                        if (reqStack.addQuantity(-toDrain) == 0) {
                            requestResources.removeStack(i);
                        }
                    }
                }
            }

            if (this.content.addQuantity(-totalActualDrained) == 0) {
                this.content = LiquidStack.EMPTY;
            }
            onContentsChanged();

            return resultFlux.addStack(LiquidStack.of(contentLiquidId, totalActualDrained));

        } finally {
            activeLock.unlock();
        }
    }
}
