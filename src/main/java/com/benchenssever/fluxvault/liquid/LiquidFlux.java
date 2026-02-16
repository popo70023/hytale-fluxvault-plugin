package com.benchenssever.fluxvault.liquid;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;

import java.util.ArrayList;
import java.util.List;

public class LiquidFlux extends AbstractFlux.Bundle<LiquidFlux, LiquidStack> {

    public LiquidFlux(LiquidStack... stacks) {
        super(stacks);
    }

    public LiquidFlux(List<LiquidStack> stacks) {
        super(stacks);
    }

    @Override
    public FluxType<LiquidFlux, LiquidStack> getFluxType() {
        return FluxType.LIQUID;
    }

    @Override
    public LiquidStack getStack(int index) {
        LiquidStack stack = super.getStack(index);
        return stack == null ? LiquidStack.EMPTY : stack;
    }

    @Override
    public void setStack(int index, LiquidStack stack) {
        if (stack == null) {
            stack = LiquidStack.EMPTY;
        }
        super.setStack(index, stack);
    }

    @Override
    public void addStack(LiquidStack stack) {
        int index = this.getIndexOf(stack);
        if (index == -1) {
            stacks.add(stack);
        } else {
            stacks.get(index).addQuantity(stack.getQuantity());
        }
    }

    @Override
    public void cleanFlux() {
        stacks.removeIf(s -> s == null || s == LiquidStack.EMPTY);
    }

    @Override
    public long getAllQuantity() {
        long allQuantity = 0;
        for (LiquidStack s : this.stacks) {
            if (s != null && !s.isEmpty()) {
                allQuantity += s.getQuantity();
            }
        }
        return allQuantity;
    }

    @Override
    public boolean matchesStack(LiquidStack stack, LiquidStack reference) {
        return stack.isLiquidEqual(reference.getLiquid());
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
