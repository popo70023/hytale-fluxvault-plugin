package com.benchenssever.fluxvault.liquid;

import com.benchenssever.fluxvault.api.FluxBundle;
import com.benchenssever.fluxvault.api.FluxType;

import java.util.ArrayList;
import java.util.List;

public class LiquidFlux extends FluxBundle<LiquidFlux, LiquidStack> {

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
    public LiquidStack getFirstStack() {
        for(LiquidStack stack : this.stacks) {
            if (stack != null && !stack.isEmpty()) {
                return stack;
            }
        }
        return null;
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
    public boolean isEmpty() {
        if (this.stacks.isEmpty()) return true;

        for (LiquidStack s : this.stacks) {
            if (!s.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public LiquidFlux copy() {
        List<LiquidStack> newStacks = new ArrayList<>(this.stacks.size());
        for (LiquidStack s : this.stacks) {
            newStacks.add(s.copy());
        }
        return new LiquidFlux(newStacks);
    }
}
