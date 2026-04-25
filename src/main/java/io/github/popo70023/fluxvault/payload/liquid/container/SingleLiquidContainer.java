/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.container;

import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.common.flux.transaction.FluxSlotTransaction;
import io.github.popo70023.fluxvault.common.flux.transaction.IFluxTransaction;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMaps;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class SingleLiquidContainer extends LiquidContainer.FixedCapacity implements IFluxHandler<LiquidFlux> {
    private LiquidStack content;
    private final short slot;

    public SingleLiquidContainer(SimpleLiquidContainer other, short slot) {
        super(other.getAcceptedHazards(), other.getWhitelist(), other.getCapacity());
        this.slot = slot;
        this.content = other.getContent(slot);
    }

    public SingleLiquidContainer(SingleLiquidContainer other) {
        super(other.getAcceptedHazards(), other.getWhitelist(), other.getCapacity());
        other.lock.readLock().lock();
        try {
            this.slot = other.slot;
            this.content = other.content.copy();
        } finally {
            other.lock.readLock().unlock();
        }
    }

    @Override
    public short getContainerMaxSize() {
        return 1;
    }

    @Override
    public Short2ObjectMap<LiquidStack> getContents() {
        lock.readLock().lock();
        try {
            return Short2ObjectMaps.singleton(slot, content.copy());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public LiquidStack getContent(short index) {
        lock.readLock().lock();
        try {
            return index == slot ? content.copy() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LiquidStack getContent() {
        return getContent(slot);
    }

    @Override
    public void setContent(short index, LiquidStack content) {
        lock.writeLock().lock();
        try {
            if (index == slot) {
                this.content = (content == null) ? LiquidStack.EMPTY : content.copy();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public short findFirstIndex() {
        return content.isEmpty() ? -1 : slot;
    }

    @Override
    public short findIndexOfTarget(LiquidStack target, boolean ignoreFull) {
        return content.isEquivalentType(target) ? slot : -1;
    }

    public void setContent(LiquidStack content) {
        setContent(slot, content);
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

            if (resource.isExact()) {
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

            LiquidStack stateBefore = this.content.copy();
            LiquidStack stateAfter;
            if (currentlyEmpty) {
                this.content = LiquidStack.of(acceptedLiquidId, totalActualFilled);
            } else {
                this.content.addQuantity(totalActualFilled);
            }
            stateAfter = this.content.copy();

            long amtBefore = stateBefore.isEmpty() ? 0 : stateBefore.getQuantity();
            long amtAfter = stateAfter.isEmpty() ? 0 : stateAfter.getQuantity();
            long delta = amtAfter - amtBefore;
            String contentId = stateBefore.isEmpty() ? stateAfter.getLiquidId() : stateBefore.getLiquidId();
            FluxSlotTransaction<LiquidStack> transaction = new FluxSlotTransaction<>(IFluxTransaction.ActionType.ADD, slot, contentId, delta, stateBefore, stateAfter);
            onContentsChanged(transaction);

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

            if (requestResources.isExact()) {
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

            LiquidStack stateBefore = this.content.copy();
            LiquidStack stateAfter;
            if (this.content.addQuantity(-totalActualDrained) == 0) {
                this.content = LiquidStack.EMPTY;
            }
            stateAfter = this.content.copy();

            long amtBefore = stateBefore.isEmpty() ? 0 : stateBefore.getQuantity();
            long amtAfter = stateAfter.isEmpty() ? 0 : stateAfter.getQuantity();
            long delta = amtAfter - amtBefore;
            String contentId = stateBefore.isEmpty() ? stateAfter.getLiquidId() : stateBefore.getLiquidId();
            FluxSlotTransaction<LiquidStack> transaction = new FluxSlotTransaction<>(IFluxTransaction.ActionType.REMOVE, slot, contentId, delta, stateBefore, stateAfter);
            onContentsChanged(transaction);

            return resultFlux.addStack(LiquidStack.of(contentLiquidId, totalActualDrained));

        } finally {
            activeLock.unlock();
        }
    }
}
