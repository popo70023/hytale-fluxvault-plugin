/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.item;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ItemContainerAdapter implements IFluxHandler<ItemFlux> {

    private final ItemContainer container;

    public ItemContainerAdapter(ItemContainer container) {
        this.container = container;
    }

    @Override
    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        return ItemFlux.class.isAssignableFrom(fluxType.getResourceClass());
    }

    @NonNullDecl
    @Override
    public ItemFlux fill(@NonNullDecl ItemFlux resource, @NonNullDecl FluxAction action) {
        ItemFlux resultFlux = new ItemFlux();
        if (resource.isEmpty()) return resultFlux;

        if (resource.isExact()) {
            ItemContainer simContainer = this.container.clone();
            for (int i = 0; i < resource.getStackCount(); i++) {
                if (resource.isIndexEmpty(i)) continue;
                ItemStack insertStack = resource.getStack(i);
                int insertQuantity = (int) Math.min(insertStack.getQuantity(), resource.getTransferLimit());
                if (insertQuantity <= 0) continue;

                ItemStackTransaction tx = simContainer.addItemStack(insertStack.withQuantity(insertQuantity), false, false, true);
                int remainder = ItemStack.isEmpty(tx.getRemainder()) ? 0 : tx.getRemainder().getQuantity();

                if (remainder > 0) {
                    return new ItemFlux();
                }
            }
        }

        ItemContainer targetContainer = action.execute() ? this.container : this.container.clone();

        for (int i = 0; i < resource.getStackCount(); ) {
            if (resource.isIndexEmpty(i)) {
                i++;
                continue;
            }

            ItemStack insertStack = resource.getStack(i);
            int insertQuantity = (int) Math.min(insertStack.getQuantity(), resource.getTransferLimit());

            if (insertQuantity <= 0) {
                i++;
                continue;
            }

            ItemStack toInsert = insertStack.withQuantity(insertQuantity);
            ItemStackTransaction tx = targetContainer.addItemStack(toInsert, false, false, true);

            boolean elementRemoved = false;

            if (tx.succeeded()) {
                int remainder = ItemStack.isEmpty(tx.getRemainder()) ? 0 : tx.getRemainder().getQuantity();
                int actuallyInserted = insertQuantity - remainder;

                if (actuallyInserted > 0) {
                    if (action.execute()) {
                        int remainingQuantity = insertStack.getQuantity() - actuallyInserted;
                        if (remainingQuantity <= 0) {
                            resource.removeStack(i);
                            elementRemoved = true;
                        } else {
                            resource.setStack(i, insertStack.withQuantity(remainingQuantity));
                        }
                    }
                    resultFlux.addStack(insertStack.withQuantity(actuallyInserted));
                }
            }

            if (!elementRemoved) {
                i++;
            }
        }

        return resultFlux;
    }

    @NonNullDecl
    @Override
    public ItemFlux drain(@NonNullDecl ItemFlux requestResources, @NonNullDecl FluxAction action) {
        ItemFlux resultFlux = new ItemFlux();
        if (this.container.isEmpty() || requestResources.isEmpty()) return resultFlux;

        if (requestResources.isExact()) {
            ItemContainer simContainer = this.container.clone();
            for (int r = 0; r < requestResources.getStackCount(); r++) {
                if (requestResources.isIndexEmpty(r)) continue;
                ItemStack requestStack = requestResources.getStack(r);
                int neededQuantity = (int) Math.min(requestStack.getQuantity(), requestResources.getTransferLimit());
                if (neededQuantity <= 0) continue;

                int extracted = 0;
                for (short slot = 0; slot < simContainer.getCapacity(); slot++) {
                    ItemStack slotStack = simContainer.getItemStack(slot);
                    if (ItemStack.isEmpty(slotStack)) continue;

                    if (requestResources.matchesWithFlux(slotStack)) {
                        int toTake = Math.min(slotStack.getQuantity(), neededQuantity - extracted);
                        ItemStackSlotTransaction tx = simContainer.removeItemStackFromSlot(slot, toTake);

                        if (tx.succeeded()) {
                            extracted += toTake;
                            if (extracted >= neededQuantity) break;
                        }
                    }
                }

                if (extracted < neededQuantity) {
                    return new ItemFlux();
                }
            }
        }

        ItemContainer targetContainer = action.execute() ? this.container : this.container.clone();

        for (int r = 0; r < requestResources.getStackCount(); ) {
            if (requestResources.isIndexEmpty(r)) {
                r++;
                continue;
            }

            ItemStack requestStack = requestResources.getStack(r);
            int remainingToExtract = (int) Math.min(requestStack.getQuantity(), requestResources.getTransferLimit());

            if (remainingToExtract <= 0) {
                r++;
                continue;
            }

            int totalExtractedForThisRequest = 0;

            for (short slot = 0; slot < targetContainer.getCapacity(); slot++) {
                ItemStack slotStack = targetContainer.getItemStack(slot);
                if (ItemStack.isEmpty(slotStack)) continue;

                if (requestResources.matchesWithFlux(slotStack)) {
                    int extractable = Math.min(slotStack.getQuantity(), remainingToExtract);
                    ItemStackSlotTransaction tx = targetContainer.removeItemStackFromSlot(slot, extractable);

                    if (tx.succeeded()) {
                        totalExtractedForThisRequest += extractable;
                        remainingToExtract -= extractable;

                        resultFlux.addStack(slotStack.withQuantity(extractable));

                        if (remainingToExtract <= 0) break;
                    }
                }
            }

            boolean elementRemoved = false;
            if (action.execute() && totalExtractedForThisRequest > 0) {
                int remainingQuantity = requestStack.getQuantity() - totalExtractedForThisRequest;
                if (remainingQuantity <= 0) {
                    requestResources.removeStack(r);
                    elementRemoved = true;
                } else {
                    requestResources.setStack(r, requestStack.withQuantity(remainingQuantity));
                }
            }

            if (!elementRemoved) {
                r++;
            }
        }

        return resultFlux;
    }
}