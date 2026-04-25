/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.item;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.popo70023.fluxvault.common.flux.AbstractFlux;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Predicate;

public class ItemFlux extends AbstractFlux.Bundle<ItemStack> {

    public ItemFlux(ItemStack... stacks) {
        super(new ObjectArrayList<>(List.of(stacks)));
    }

    public ItemFlux(List<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public ItemFlux addStack(ItemStack stack) {
        if (ItemStack.isEmpty(stack)) return this;

        int index = this.findIndexOfTarget(stack);
        if (index == -1) {
            this.stacks.add(stack);
        } else {
            ItemStack oldStack = this.stacks.get(index);
            this.stacks.set(index, oldStack.withQuantity(oldStack.getQuantity() + stack.getQuantity()));
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
            if (!ItemStack.isEmpty(s)) return false;
        }

        return true;
    }

    @Override
    public boolean isIndexEmpty(int index) {
        return ItemStack.isEmpty(getStack(index));
    }

    @Override
    public boolean matchesWithIndex(int index, ItemStack reference) {
        return ItemStack.isEquivalentType(getStack(index), reference);
    }

    @Override
    public ItemFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    @Override
    public ItemFlux withValidator(Predicate<ItemStack> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public ItemFlux withExact(boolean exact) {
        setExact(exact);
        return this;
    }

    @Override
    public ItemFlux copy() {
        ItemFlux copy = new ItemFlux(stacks);
        copy.attributes = this.attributes.copy();
        return copy;
    }
}
