package com.benchenssever.fluxvault.item;

import com.benchenssever.fluxvault.api.FluxBundle;
import com.benchenssever.fluxvault.api.FluxType;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFlux extends FluxBundle<ItemFlux, ItemStack> {

    public ItemFlux(ItemStack... stacks) {
        super(stacks);
    }

    public ItemFlux(List<ItemStack> stacks) {
        super(stacks);
    }

    /**
     * Creates a deep copy of a Hytale ItemStack.
     * <p>
     * Preserves strict null/empty states as per implementation requirements.
     * </p>
     *
     * @param original The stack to copy.
     * @return A new independent ItemStack, ItemStack.EMPTY, or null.
     */
    public static ItemStack copyStack(ItemStack original) {
        if (original == null) return null;
        if (original.isEmpty()) return ItemStack.EMPTY;
        ItemStack newStack = new ItemStack(
                original.getItemId(),
                original.getQuantity(),
                original.getDurability(),
                original.getMaxDurability(),
                original.getMetadata()
        );
        newStack.setOverrideDroppedItemAnimation(original.getOverrideDroppedItemAnimation());

        return newStack;
    }

    @Override
    public FluxType<ItemFlux, ItemStack> getFluxType() {
        return FluxType.ITEM;
    }

    @Override
    public ItemStack getFirstStack() {
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty()) {
                return stack;
            }
        }
        return null;
    }

    @Override
    public long getAllQuantity() {
        long allQuantity = 0;
        for (ItemStack s : stacks) {
            if (s != null && !s.isEmpty()) {
                allQuantity += s.getQuantity();
            }
        }
        return allQuantity;
    }

    @Override
    public boolean isEmpty() {
        if (stacks.isEmpty()) return true;

        for (ItemStack s : stacks) {
            if (!s.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public ItemFlux copy() {
        List<ItemStack> newStacks = new ArrayList<>(stacks.size());

        for (ItemStack s : stacks) {
            newStacks.add(copyStack(s));
        }
        return new ItemFlux(newStacks);
    }
}
