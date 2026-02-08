package com.benchenssever.fluxvault.item;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

//TODO: Implement merging and stacking logic in addStack, ensuring that items with the same ID and compatible metadata are combined according to Hytale's mechanics.
public class ItemFlux extends AbstractFlux.Bundle<ItemFlux, ItemStack> {

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
    public ItemStack getStack(int index) {
        ItemStack stack = super.getStack(index);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        if (stack == null) {
            stack = ItemStack.EMPTY;
        }
        super.setStack(index, stack);
    }

    @Override
    public FluxType<ItemFlux, ItemStack> getFluxType() {
        return FluxType.ITEM;
    }

    //TODO: Implement setStack and addStack to handle merging and stacking logic according to Hytale's item mechanics.
    @Override
    public void addStack(ItemStack stack) {

    }

    //TODO: Implement cleanFlux to remove null or empty stacks from the internal list, ensuring the flux remains compact and efficient.
    @Override
    public void cleanFlux() {

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
    public boolean matcheStack(ItemStack stack, ItemStack reference) {
        return stack.getItem().equals(reference.getItem());
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
