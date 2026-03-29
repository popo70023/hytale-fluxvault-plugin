package io.github.popo70023.fluxvault.item;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.popo70023.fluxvault.api.AbstractFlux;
import io.github.popo70023.fluxvault.api.FluxType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ItemFlux extends AbstractFlux.Bundle<ItemStack> {

    public ItemFlux(ItemStack... stacks) {
        super(stacks);
    }

    public ItemFlux(List<ItemStack> stacks) {
        super(stacks);
    }

    public static FluxType<ItemFlux, ItemStack> getFluxType() {
        return FluxType.ITEM;
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

    public ItemFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    public ItemFlux withValidator(Predicate<ItemStack> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public ItemFlux copy() {
        return new ItemFlux(new ArrayList<>(this.stacks));
    }
}
