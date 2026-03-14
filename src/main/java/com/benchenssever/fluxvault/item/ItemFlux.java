package com.benchenssever.fluxvault.item;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

//TODO: Implement merging and stacking logic in addStack, ensuring that items with the same ID and compatible metadata are combined according to Hytale's mechanics.
public class ItemFlux extends AbstractFlux.Bundle<ItemStack> {

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

    public static FluxType<ItemFlux, ItemStack> getFluxType() {
        return FluxType.ITEM;
    }

    @Override
    public ItemFlux addStack(ItemStack... stacks) {
        if (stacks == null) return this;
        for (ItemStack stack : stacks) {
            int index = this.findIndexOfTarget(stack);
            if (index == -1) {
                this.stacks.add(stack);
            } else {
                ItemStack oldStack = this.stacks.get(index);
                this.stacks.set(index, new ItemStack(oldStack.getItemId(), oldStack.getQuantity() + stack.getQuantity(), oldStack.getMetadata()));
            }
        }
        return this;
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
            if (s == null) continue;
            if (!s.isEmpty() || s.getQuantity() > 0) return false;
        }

        return true;
    }

    @Override
    public boolean isIndexEmpty(int index) {
        return getStack(index) == null || getStacks().isEmpty();
    }

    @Override
    public boolean matchesWithIndex(int index, ItemStack reference) {
        return getStack(index).isEquivalentType(reference);
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
