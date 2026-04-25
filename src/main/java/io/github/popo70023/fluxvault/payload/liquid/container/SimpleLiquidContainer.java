/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.container;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.Short2ObjectMapCodec;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.common.flux.AbstractContainer;
import io.github.popo70023.fluxvault.common.flux.transaction.FluxListTransaction;
import io.github.popo70023.fluxvault.common.flux.transaction.FluxSlotTransaction;
import io.github.popo70023.fluxvault.common.flux.transaction.IFluxTransaction;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import it.unimi.dsi.fastutil.shorts.*;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SimpleLiquidContainer extends LiquidContainer.FixedCapacity implements IFluxHandler<LiquidFlux> {
    public static final BuilderCodec<SimpleLiquidContainer> CODEC;

    private short size;
    private Short2ObjectMap<LiquidStack> contents;

    public SimpleLiquidContainer() {
        super(Collections.emptySet(), Collections.emptySet(), 10000);
        this.size = 1;
        this.contents = new Short2ObjectOpenHashMap<>(size);
    }

    public SimpleLiquidContainer(BluePrint bp) {
        super(Set.of(bp.acceptedHazards), Set.of(bp.whitelist), bp.capacity);
        this.size = bp.size;
        this.contents = new Short2ObjectOpenHashMap<>(size);
    }

    public SimpleLiquidContainer(SimpleLiquidContainer other) {
        super(other.getAcceptedHazards(), other.getWhitelist(), other.getCapacity());
        other.lock.readLock().lock();
        try {
            this.size = other.size;
            this.contents = new Short2ObjectOpenHashMap<>(size);
            for (Short2ObjectMap.Entry<LiquidStack> entry : other.contents.short2ObjectEntrySet()) {
                LiquidStack stack = entry.getValue();
                if (stack != null && !stack.isEmpty()) {
                    this.contents.put(entry.getShortKey(), stack.copy());
                }
            }
        } finally {
            other.lock.readLock().unlock();
        }
    }

    @Override
    public short getContainerMaxSize() {
        return size;
    }

    @Override
    public Short2ObjectMap<LiquidStack> getContents() {
        lock.readLock().lock();
        try {
            return contents;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public LiquidStack getContent(short index) {
        lock.readLock().lock();
        try {
            LiquidStack stack = contents.get(index);
            return stack == null ? LiquidStack.EMPTY : stack.copy();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void setContent(short index, LiquidStack content) {
        if (index < 0 || index >= getContainerMaxSize()) return;

        lock.writeLock().lock();
        try {
            if (content == null || content.isEmpty()) {
                contents.remove(index);
            } else {
                contents.put(index, content.copy());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public short findFirstIndex() {
        lock.readLock().lock();
        try {
            final short[] minIndex = {Short.MAX_VALUE};

            this.contents.forEach((slot, stack) -> {
                if (!LiquidStack.isEmpty(stack)) {
                    if (slot < minIndex[0]) {
                        minIndex[0] = slot;
                    }
                }
            });

            return minIndex[0] == Short.MAX_VALUE ? -1 : minIndex[0];
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public short findIndexOfTarget(LiquidStack target, boolean ignoreFull) {
        if (target == null || target.isEmpty()) return -1;
        lock.readLock().lock();
        try {
            for (Short2ObjectMap.Entry<LiquidStack> entry : this.contents.short2ObjectEntrySet()) {
                LiquidStack theContent = entry.getValue();

                if (theContent.isEquivalentType(target)) {
                    if (!ignoreFull || theContent.getQuantity() < getCapacity()) {
                        return entry.getShortKey();
                    }
                }
            }
            return -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long getAllContentQuantity() {
        long count = 0;
        lock.readLock().lock();
        try {
            for (LiquidStack content : contents.values()) {
                count += content.getQuantity();
            }
            return count;
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
            if (resource.isEmpty()) return resultFlux;

            long totalFilled = 0;
            long limit = resource.getTransferLimit();

            Short2ObjectMap<LiquidStack> simulatedMap = new Short2ObjectOpenHashMap<>();
            long[] filledFromReq = new long[resource.getStackCount()];

            for (int reqIdx = 0; reqIdx < resource.getStackCount(); reqIdx++) {
                LiquidStack reqStack = resource.getStack(reqIdx);
                if (reqStack.isEmpty() || !matchesWithContainer(reqStack)) continue;

                long toFill = reqStack.getQuantity();

                ShortSet activeSlots = new ShortOpenHashSet(this.contents.keySet());
                activeSlots.addAll(simulatedMap.keySet());

                ShortIterator iterator = activeSlots.iterator();

                while (iterator.hasNext()) {
                    short slot = iterator.nextShort();

                    if (toFill <= 0 || totalFilled >= limit) break;

                    LiquidStack currentState = getSimulatedState(slot, simulatedMap);

                    if (!currentState.isEmpty() && currentState.isEquivalentType(reqStack)) {
                        long space = getCapacity() - currentState.getQuantity();
                        if (space > 0) {
                            long fillAmt = Math.min(Math.min(toFill, space), limit - totalFilled);
                            if (fillAmt > 0) {
                                LiquidStack newState = currentState.copy();
                                newState.addQuantity(fillAmt);
                                simulatedMap.put(slot, newState);

                                toFill -= fillAmt;
                                totalFilled += fillAmt;
                                filledFromReq[reqIdx] += fillAmt;
                            }
                        }
                    }
                }

                if (toFill > 0 && totalFilled < limit) {
                    for (Short slot = 0; slot < getContainerMaxSize(); slot++) {
                        if (toFill <= 0 || totalFilled >= limit) break;

                        LiquidStack currentState = getSimulatedState(slot, simulatedMap);
                        if (currentState.isEmpty()) {
                            long fillAmt = Math.min(Math.min(toFill, getCapacity()), limit - totalFilled);
                            if (fillAmt > 0) {
                                LiquidStack newState = LiquidStack.of(reqStack.getLiquidId(), fillAmt);
                                simulatedMap.put(slot, newState);

                                toFill -= fillAmt;
                                totalFilled += fillAmt;
                                filledFromReq[reqIdx] += fillAmt;
                            }
                        }
                    }
                }
            }

            if (totalFilled <= 0) return resultFlux;

            if (resource.isExact() && totalFilled < limit && totalFilled < resource.getAllQuantity()) {
                return new LiquidFlux();
            }

            List<FluxSlotTransaction<LiquidStack>> transactions = new ArrayList<>();
            for (Short2ObjectMap.Entry<LiquidStack> entry : simulatedMap.short2ObjectEntrySet()) {
                Short slot = entry.getShortKey();
                LiquidStack stateBefore = this.contents.get(slot);
                stateBefore = (stateBefore == null) ? LiquidStack.EMPTY : stateBefore.copy();
                LiquidStack stateAfter = entry.getValue();

                long amtBefore = stateBefore.isEmpty() ? 0 : stateBefore.getQuantity();
                long amtAfter = stateAfter.isEmpty() ? 0 : stateAfter.getQuantity();
                long delta = amtAfter - amtBefore;
                String contentId = stateBefore.isEmpty() ? stateAfter.getLiquidId() : stateBefore.getLiquidId();
                transactions.add(new FluxSlotTransaction<>(IFluxTransaction.ActionType.ADD, slot, contentId, delta, stateBefore, stateAfter));
            }

            if (!transactions.isEmpty()) {
                for (FluxSlotTransaction<LiquidStack> tx : transactions) {
                    resultFlux.addStack(LiquidStack.of(tx.contentId(), tx.deltaAmount()));
                }

                if (action.execute()) {
                    for (FluxSlotTransaction<LiquidStack> tx : transactions) {
                        this.contents.put(tx.slotIndex(), tx.stateAfter());
                    }

                    onContentsChanged(new FluxListTransaction(transactions));

                    for (int i = resource.getStackCount() - 1; i >= 0; i--) {
                        if (filledFromReq[i] > 0) {
                            if (resource.getStack(i).addQuantity(-filledFromReq[i]) == 0) {
                                resource.removeStack(i);
                            }
                        }
                    }
                }
            }

            return resultFlux;
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
            if (this.contents.isEmpty() || requestResources.isEmpty()) return resultFlux;

            long totalDrained = 0;
            long limit = requestResources.getTransferLimit();

            Short2ObjectMap<LiquidStack> simulatedMap = new Short2ObjectOpenHashMap<>();
            java.util.Map<String, Long> extractedTotals = new java.util.HashMap<>();

            long[] drainedFromReq = new long[requestResources.getStackCount()];

            for (int reqIdx = 0; reqIdx < requestResources.getStackCount(); reqIdx++) {
                LiquidStack reqStack = requestResources.getStack(reqIdx);
                if (reqStack.getQuantity() <= 0 || !requestResources.matchesWithFlux(reqStack)) continue;

                long toDrain = reqStack.getQuantity();
                String requestedId = reqStack.getLiquidId();
                boolean isWildcard = requestedId.equals(Liquid.EMPTY_ID);

                ShortSet activeSlots = new ShortOpenHashSet(this.contents.keySet());
                activeSlots.addAll(simulatedMap.keySet());

                ShortIterator iterator = activeSlots.iterator();

                while (iterator.hasNext()) {
                    short slot = iterator.nextShort();
                    if (toDrain <= 0 || totalDrained >= limit) break;

                    LiquidStack currentState = getSimulatedState(slot, simulatedMap);
                    if (currentState.isEmpty()) continue;

                    if (isWildcard || currentState.getLiquidId().equals(requestedId)) {
                        long availableInSlot = currentState.getQuantity();
                        long drainAmt = Math.min(Math.min(toDrain, availableInSlot), limit - totalDrained);

                        if (drainAmt > 0) {
                            String actualLiquidId = currentState.getLiquidId();

                            LiquidStack newState = currentState.copy();
                            if (newState.addQuantity(-drainAmt) == 0) {
                                simulatedMap.put(slot, LiquidStack.EMPTY);
                            } else {
                                simulatedMap.put(slot, newState);
                            }

                            toDrain -= drainAmt;
                            totalDrained += drainAmt;
                            extractedTotals.put(actualLiquidId, extractedTotals.getOrDefault(actualLiquidId, 0L) + drainAmt);

                            drainedFromReq[reqIdx] += drainAmt;
                        }
                    }
                }
            }

            if (totalDrained <= 0) return resultFlux;

            if (requestResources.isExact() && totalDrained < limit && totalDrained < requestResources.getAllQuantity()) {
                return new LiquidFlux();
            }

            List<FluxSlotTransaction<LiquidStack>> transactions = new ArrayList<>();
            for (Short2ObjectMap.Entry<LiquidStack> entry : simulatedMap.short2ObjectEntrySet()) {
                Short slot = entry.getShortKey();
                LiquidStack stateBefore = this.contents.get(slot);
                stateBefore = (stateBefore == null) ? LiquidStack.EMPTY : stateBefore.copy();
                LiquidStack stateAfter = entry.getValue();

                long amtBefore = stateBefore.isEmpty() ? 0 : stateBefore.getQuantity();
                long amtAfter = stateAfter.isEmpty() ? 0 : stateAfter.getQuantity();
                long delta = amtAfter - amtBefore;

                String contentId = stateBefore.isEmpty() ? stateAfter.getLiquidId() : stateBefore.getLiquidId();

                transactions.add(new FluxSlotTransaction<>(
                        IFluxTransaction.ActionType.REMOVE,
                        slot,
                        contentId,
                        delta,
                        stateBefore,
                        stateAfter
                ));
            }

            if (!transactions.isEmpty()) {
                for (FluxSlotTransaction<LiquidStack> tx : transactions) {
                    long extractedAmt = Math.abs(tx.deltaAmount());
                    resultFlux.addStack(LiquidStack.of(tx.contentId(), extractedAmt));
                }
                if (action.execute()) {
                    for (FluxSlotTransaction<LiquidStack> tx : transactions) {
                        LiquidStack after = tx.stateAfter();
                        if (after.isEmpty()) {
                            this.contents.remove(tx.slotIndex());
                        } else {
                            this.contents.put(tx.slotIndex(), after);
                        }
                    }

                    onContentsChanged(new FluxListTransaction(transactions));

                    for (int i = requestResources.getStackCount() - 1; i >= 0; i--) {
                        if (drainedFromReq[i] > 0) {
                            if (requestResources.getStack(i).addQuantity(-drainedFromReq[i]) == 0) {
                                requestResources.removeStack(i);
                            }
                        }
                    }
                }
            }

            return resultFlux;

        } finally {
            activeLock.unlock();
        }
    }

    private LiquidStack getSimulatedState(short slot, Short2ObjectMap<LiquidStack> simulatedMap) {
        if (simulatedMap.containsKey(slot)) {
            return simulatedMap.get(slot);
        }
        LiquidStack realState = this.contents.get(slot);
        return realState == null ? LiquidStack.EMPTY : realState;
    }

    public static class BluePrint {
        private String[] acceptedHazards = new String[0];
        private String[] whitelist = new String[0];
        private long capacity = 10000;
        private short size = 1;

        public static final BuilderCodec<BluePrint> CODEC = BuilderCodec.builder(BluePrint.class, BluePrint::new)
                .append(new KeyedCodec<>(LiquidContainer.ACCEPTED_HAZARDS_KEY, Codec.STRING_ARRAY), (bp, v) -> bp.acceptedHazards = v, BluePrint::getAcceptedHazards)
                .documentation(LiquidContainer.ACCEPTED_HAZARDS_DOCUMENTATION).add()
                .append(new KeyedCodec<>(LiquidContainer.WHITELIST_KEY, Codec.STRING_ARRAY), (bp, v) -> bp.whitelist = v, BluePrint::getWhitelist)
                .documentation(LiquidContainer.WHITELIST_DOCUMENTATION).add()
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), (bp, v) -> bp.capacity = v, BluePrint::getCapacity)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.SIZE_KEY, Codec.SHORT), (bp, v) -> bp.size = v, BluePrint::getSize)
                .add()
                .build();

        public String[] getAcceptedHazards() {
            return acceptedHazards;
        }

        public String[] getWhitelist() {
            return whitelist;
        }

        public long getCapacity() {
            return capacity;
        }

        public short getSize() {
            return size;
        }

        public BluePrint copy() {
            BluePrint copy = new BluePrint();

            copy.acceptedHazards = this.acceptedHazards != null ? this.acceptedHazards.clone() : new String[0];
            copy.whitelist = this.whitelist != null ? this.whitelist.clone() : new String[0];
            copy.capacity = this.capacity;
            copy.size = this.size;

            return copy;
        }
    }

    static {
        CODEC = BuilderCodec.builder(SimpleLiquidContainer.class, SimpleLiquidContainer::new)
                .append(new KeyedCodec<>(LiquidContainer.ACCEPTED_HAZARDS_KEY, Codec.STRING_ARRAY), (container, v) -> container.setAcceptedHazards(v != null ? Set.of(v) : null), (container) -> container.getAcceptedHazards().toArray(String[]::new))
                .documentation(LiquidContainer.ACCEPTED_HAZARDS_DOCUMENTATION).add()
                .append(new KeyedCodec<>(LiquidContainer.WHITELIST_KEY, Codec.STRING_ARRAY), (container, v) -> container.setWhitelist(v != null ? Set.of(v) : null), (container) -> container.getWhitelist().toArray(String[]::new))
                .documentation(LiquidContainer.WHITELIST_DOCUMENTATION).add()
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), SimpleLiquidContainer::setCapacity, SimpleLiquidContainer::getCapacity)
                .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
                .append(new KeyedCodec<>(AbstractContainer.SIZE_KEY, Codec.SHORT), (container, v) -> container.size = v, SimpleLiquidContainer::getContainerMaxSize)
                .documentation(AbstractContainer.SIZE_DOCUMENTATION).add()
                .append(new KeyedCodec<>(AbstractContainer.CONTENTS_KEY, new Short2ObjectMapCodec<>(LiquidStack.CODEC, Short2ObjectOpenHashMap::new, false)), (container, v) -> container.contents = v != null ? new Short2ObjectOpenHashMap<>(v) : new Short2ObjectOpenHashMap<>(container.size), (container) -> container.contents)
                .add()
                .build();
    }
}
