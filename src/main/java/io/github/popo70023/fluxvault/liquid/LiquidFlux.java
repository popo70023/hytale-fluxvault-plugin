/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.liquid;

import io.github.popo70023.fluxvault.api.AbstractFlux;
import io.github.popo70023.fluxvault.api.FluxType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LiquidFlux extends AbstractFlux.Bundle<LiquidStack> {

    public LiquidFlux(LiquidStack... stacks) {
        super(new ArrayList<>(List.of(stacks)));
    }

    public LiquidFlux(List<LiquidStack> stacks) {
        super(stacks);
    }

    public static FluxType<LiquidFlux, LiquidStack> getFluxType() {
        return FluxType.LIQUID;
    }

    public static LiquidFlux copyOf(LiquidStack... stacks) {
        List<LiquidStack> copies = new ArrayList<>(stacks.length);
        for (LiquidStack s : stacks) {
            if (s != null) copies.add(s.copy());
        }
        return new LiquidFlux(copies);
    }

    @Override
    public LiquidFlux addStack(LiquidStack stack) {
        if (stack == null) return this;

        int index = this.findIndexOfTarget(stack);
        if (index == -1) {
            this.stacks.add(stack);
        } else {
            this.stacks.get(index).addQuantity(stack.getQuantity());
        }
        return this;
    }

    @Override
    public long getAllQuantity() {
        long allQuantity = 0;
        for (LiquidStack s : stacks) {
            if (s != null && !s.isEmpty()) {
                allQuantity += s.getQuantity();
            }
        }
        return allQuantity;
    }

    @Override
    public boolean isEmpty() {
        if (stacks.isEmpty()) return true;

        for (LiquidStack s : stacks) {
            if (s == null) continue;
            if (!s.isEmpty() || s.getQuantity() > 0) return false;
        }

        return true;
    }

    @Override
    public boolean isIndexEmpty(int index) {
        return getStack(index) == null || getStack(index).getQuantity() <= 0;
    }

    @Override
    public boolean matchesWithIndex(int index, LiquidStack reference) {
        return getStack(index).isEquivalentType(reference);
    }

    public LiquidFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    public LiquidFlux withValidator(Predicate<LiquidStack> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public LiquidFlux copy() {
        List<LiquidStack> newStacks = new ArrayList<>(this.stacks.size());
        for (LiquidStack s : this.stacks) {
            newStacks.add(s.copy());
        }
        return new LiquidFlux(newStacks);
    }

    @Override
    public String toString() {
        return "LiquidFlux{stacks=" + stacks + '}';
    }
}
