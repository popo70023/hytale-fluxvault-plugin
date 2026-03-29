package io.github.popo70023.fluxvault.item;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ItemContainerAdapter implements IFluxHandler<ItemFlux> {

    private final ItemContainer container;

    public ItemContainerAdapter(ItemContainer container) {
        this.container = container;
    }

    @NonNullDecl
    @Override
    public ItemFlux fill(@NonNullDecl ItemFlux resource, @NonNullDecl FluxAction action) {
        ItemFlux resultFlux = new ItemFlux();
        if (resource.isEmpty()) return resultFlux;

        ItemContainer targetContainer = action.execute() ? this.container : this.container.clone();

        for (int i = 0; i < resource.getStackCount(); i++) {
            if (resource.isIndexEmpty(i)) continue;

            ItemStack insertStack = resource.getStack(i);
            long limit = resource.getTransferLimit();
            int insertQuantity = (int) Math.min(insertStack.getQuantity(), limit);

            if (insertQuantity <= 0) continue;

            ItemStack toInsert = insertStack.withQuantity(insertQuantity);

            ItemStackTransaction tx = targetContainer.addItemStack(toInsert, false, false, true);

            if (tx.succeeded()) {
                int remainder = ItemStack.isEmpty(tx.getRemainder()) ? 0 : tx.getRemainder().getQuantity();
                int actuallyInserted = insertQuantity - remainder;

                if (actuallyInserted > 0) {
                    if (action.execute()) {
                        int remainingQuantity = insertStack.getQuantity() - actuallyInserted;
                        if (remainingQuantity <= 0) {
                            resource.removeStack(i);
                        } else {
                            resource.setStack(i, insertStack.withQuantity(remainingQuantity));
                        }
                    }
                    resultFlux.addStack(insertStack.withQuantity(actuallyInserted));
                }
            }
        }

        return resultFlux;
    }

    @NonNullDecl
    @Override
    public ItemFlux drain(@NonNullDecl ItemFlux requestResources, @NonNullDecl FluxAction action) {
        ItemFlux resultFlux = new ItemFlux();
        if (this.container.isEmpty() || requestResources.isEmpty()) return resultFlux;

        ItemContainer targetContainer = action.execute() ? this.container : this.container.clone();

        for (int r = 0; r < requestResources.getStackCount(); r++) {
            if (requestResources.isIndexEmpty(r)) continue;

            ItemStack requestStack = requestResources.getStack(r);
            long limit = requestResources.getTransferLimit();
            int remainingToExtract = (int) Math.min(requestStack.getQuantity(), limit);

            if (remainingToExtract <= 0) continue;

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

            if (action.execute() && totalExtractedForThisRequest > 0) {
                int remainingQuantity = requestStack.getQuantity() - totalExtractedForThisRequest;
                if (remainingQuantity <= 0) {
                    requestResources.removeStack(r);
                } else {
                    requestResources.setStack(r, requestStack.withQuantity(remainingQuantity));
                }
            }
        }

        return resultFlux;
    }
}