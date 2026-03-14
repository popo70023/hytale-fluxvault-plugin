package com.benchenssever.fluxvault.liquid;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;

import java.util.ArrayList;
import java.util.List;

public class LiquidFlux extends AbstractFlux.Bundle<LiquidStack> {

    public LiquidFlux(LiquidStack... stacks) {
        super(stacks);
    }

    public LiquidFlux(List<LiquidStack> stacks) {
        super(stacks);
    }

    public static FluxType<LiquidFlux, LiquidStack> getFluxType() {
        return FluxType.LIQUID;
    }

    @Override
    public LiquidFlux addStack(LiquidStack... stacks) {
        if (stacks == null) return this;

        for (LiquidStack stack : stacks) {
            int index = this.findIndexOfTarget(stack);
            if (index == -1) {
                this.stacks.add(stack);
            } else {
                this.stacks.get(index).addQuantity(stack.getQuantity());
            }
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
